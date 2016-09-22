package cassie.pagestats

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer

import org.joda.time.Duration

import akka.actor.{Actor, ActorLogging, Props, ActorRef}
import akka.pattern.pipe
import akka.routing.FromConfig
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianash.commons.behavior.Behavior

import cassie.core.structures._
import cassie.core.protocols.customer._
import cassie.core.protocols.pagestats._

import cassie.customer.CustomerService

import cassie.pagestats.datastore.PageStatDatastore
import cassie.pagestats.connector.PageStatConnector
import cassie.pagestats.model.PageReferral


class PageStatService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = PageStatSettings(context.system)
  private val connector = new PageStatConnector(settings)
  private val datastore = new PageStatDatastore(connector)
  datastore.init()

  private val customer = context.actorOf(FromConfig.props, "customer")
  context watch customer

  def receive = {

    case UpdatePageVisitStats(tokenId, pageIdTo, instanceId, aianid, pageIdFrom) =>
      datastore.updatePageVisitStats(tokenId, pageIdTo, instanceId, aianid, pageIdFrom) pipeTo sender()

    case UpdatePageValueStats(tokenId, pageId, instanceId, avgDwellTime) =>
      datastore.updatePageValueStats(tokenId, pageId, instanceId, avgDwellTime) pipeTo sender()

    case GetPageStats(tokenId, pageId, instanceId) =>
      datastore.getPageStats(tokenId, pageId, instanceId).flatMap {
        case (pageCountStatO, pageValueStatO, prevPageReferrals, nextPageReferrals) =>
          for {
            prevReferrals <- getReferralList(tokenId, prevPageReferrals, customer)
            nextReferrals <- getReferralList(tokenId, nextPageReferrals, customer)
          } yield {
            val avgDwellTime = new Duration(pageValueStatO.get.avgDwellTime)
            PageStats(tokenId, pageId, instanceId, Behavior.Stats(
              Behavior.PageViews(pageCountStatO.get.pageViews),
              Behavior.Visitors(pageCountStatO.get.totalVisitors),
              Behavior.Visitors(pageCountStatO.get.newVisitors),
              avgDwellTime,
              prevReferrals,
              nextReferrals))
          }
      } pipeTo sender()

    }

  private def getReferralList(tokenId: Long, pageReferrals: Seq[PageReferral], customer: ActorRef) = {
    if(!pageReferrals.isEmpty) {
      var referrals = ArrayBuffer.empty[Future[Behavior.Referral]]
      for(pageReferral <- pageReferrals) {
        val refPageId = pageReferral.refPageId
        val refCount = pageReferral.refCount

        implicit val timeout = Timeout(2 seconds)
        val webPageF = (customer ?= GetWebPageById(tokenId, refPageId))
        referrals += webPageF.map {webPage =>
          Behavior.Referral(refPageId, webPage.get.name, refCount, webPage.get.url)
        }
      }
      Future.sequence(referrals)
    } else {
      Future.successful(Seq.empty[Behavior.Referral])
    }
  }

}

object PageStatService {

  def props = Props(classOf[PageStatService])

}
