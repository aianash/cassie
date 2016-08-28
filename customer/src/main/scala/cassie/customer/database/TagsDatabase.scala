package cassie.customer.database

import com.websudos.phantom.dsl._

import cassie.customer.model.Tags


class TagsDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object Tags extends Tags with connector.Connector

}