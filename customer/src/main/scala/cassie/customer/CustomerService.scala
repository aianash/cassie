package cassie.customer

import scala.concurrent.duration._
import scala.concurrent.Future

import java.net.URL

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianonymous.commons.core.services.{UUIDGenerator, NextId}
import aianonymous.commons.customer.{Domain, WebPage}

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

    case GetOrCreatePageId(url, tokenId, name) =>
      datastore.getWebPage(url).flatMap {
        _.EITHER {
          webpage => Future.successful(webpage.pageId)
        } OR {
          implicit val timeout = Timeout(2 seconds)
          val webpageF = (uuid ?= NextId("webpage")) map { id => WebPage(tokenId, id.get, url, name) }
          webpageF flatMap { webpage =>
            datastore.insertWebPage(webpage).map(_ => webpage.pageId)
          }
        }
      } pipeTo sender()

    case GetPageId(url) =>
      datastore.getWebPage(url) map { webpageO =>
        webpageO.map(_.pageId)
      } pipeTo sender()

    case GetWebPage(url) =>
      datastore.getWebPage(url) pipeTo sender()
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

}

object CustomerService {

  def props = Props(classOf[CustomerService])

}