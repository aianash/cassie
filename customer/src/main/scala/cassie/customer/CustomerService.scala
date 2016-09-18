package cassie.customer

import scala.concurrent.duration._
import scala.concurrent.Future

import java.net.URL

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianonymous.commons.core.services.{UUIDGenerator, NextId}
import aianonymous.commons.customer.{Domain, PageURL}

import cassie.core.protocols.customer._

import cassie.customer.datastore.CustomerDatastore
import cassie.customer.connector.CustomerConnector


class CustomerService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings  = CustomerSettings(context.system)
  private val connector = new CustomerConnector(settings)
  private val datastore = new CustomerDatastore(connector)
  datastore.init()

  private val uuid = context.actorOf(UUIDGenerator.props(settings.ServiceId, settings.DatacenterId))
  context watch uuid

  def receive = {
    case InsertPageTags(tags) =>
      datastore.insertTags(tags) pipeTo sender()

    case FetchPageTags(tid, pid) =>
      datastore.getTagsFor(tid, pid) pipeTo sender()

    case GetDomain(name) =>
      datastore.getDomainFor(name) pipeTo sender()

    case GetOrCreatePageId(url, tokenId) =>
      val urlstr = getUrlString(url)
      datastore.getPageUrl(urlstr).flatMap {
        _.EITHER {
          pageurl => Future.successful(pageurl.pageId)
        } OR {
          implicit val timeout = Timeout(2 seconds)
          val pageurlF = (uuid ?= NextId("pageurl")) map { id => PageURL(tokenId, id.get, urlstr) }
          pageurlF flatMap { pageurl =>
            datastore.insertPageUrl(pageurl).map(_ => pageurl.pageId)
          }
        }
      } pipeTo sender()

    case GetPageId(url) =>
      val urlstr = getUrlString(url)
      datastore.getPageUrl(urlstr) map { urlO =>
        urlO.map(_.pageId)
      } pipeTo sender()

    case GetPageURL(url) =>
      val urlstr = getUrlString(url)
      datastore.getPageUrl(urlstr) pipeTo sender()
  }

  implicit class OptionEitherOr[T](opt: Option[T]) {
    def EITHER[R](either: T => R) =
      new {
        def OR(or: => R) =
          opt match {
            case Some(a) => either(a)
            case None => or
          }
      }
  }

  private def getUrlString(url: URL) =
    url.getHost +
    (if(url.getPort != -1) url.getPort else "") +
    url.getPath

}

object CustomerService {

  def props = Props(classOf[CustomerService])

}