package cassie.customer

import akka.actor.{Actor, ActorLogging}
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
    case InsertTags(tags) => datastore.insertTags(tags) pipeTo sender()
  }

}