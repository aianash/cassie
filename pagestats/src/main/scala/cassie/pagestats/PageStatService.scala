package cassie.pagestats

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.routing.FromConfig

import cassie.core.protocols.pagestats._

import cassie.customer.CustomerService

import cassie.pagestats.datastore.PageStatDatastore
import cassie.pagestats.connector.PageStatConnector


class PageStatService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = PageStatSettings(context.system)
  private val connector = new PageStatConnector(settings)
  private val datastore = new PageStatDatastore(connector)
  datastore.init()

  private val customer = context.actorOf(FromConfig.props, "customer-service")
  context watch customer

  def receive = {

    case UpdatePageVisitStats(tokenId, pageIdTo, instanceId, aianid, pageIdFrom) =>
      datastore.updatePageVisitStats(tokenId, pageIdTo, instanceId, aianid, pageIdFrom) pipeTo sender()

    case UpdatePageValueStats(tokenId, pageId, instanceId, avgDwellTime) =>
      datastore.updatePageValueStats(tokenId, pageId, instanceId, avgDwellTime) pipeTo sender()

    case GetPageStats(tokenId, pageId, instanceId) =>
      datastore.getPageStats(tokenId, pageId, instanceId, customer) pipeTo sender()

  }

}

object PageStatService {

  def props = Props(classOf[PageStatService])

}
