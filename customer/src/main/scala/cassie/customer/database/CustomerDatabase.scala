package cassie.customer.database

import com.websudos.phantom.dsl._

import cassie.customer.model._


class CustomerDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object Tags extends Tags with connector.Connector
  object Domains extends Domains with connector.Connector
  object WebPagesByURL extends WebPagesByURL with connector.Connector

}