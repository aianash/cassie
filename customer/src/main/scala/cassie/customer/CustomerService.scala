package cassie.customer

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import cassie.core.protocols.customer._

import cassie.customer.datastore.TagsDatastore
import cassie.customer.connector.CustomerConnector


class CustomerService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings  = CustomerSettings(context.system)
  private val connector = new CustomerConnector(settings)
  private val datastore = new TagsDatastore(connector)
  datastore.init()

  def receive = {
    case InsertPageTags(tags) =>
      datastore.insertTags(tags) pipeTo sender()

    case FetchPageTags(tid, pid) =>
      datastore.getTagsFor(tid, pid) pipeTo sender()
  }

}

object CustomerService {

  def props = Props(classOf[CustomerService])

}