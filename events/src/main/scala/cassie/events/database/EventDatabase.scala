package cassie.events.database

import com.websudos.phantom.dsl._

import cassie.events.model.{Events, Sessions}

class EventDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object Sessions extends Sessions with connector.Connector
  object Events extends Events with connector.Connector
}