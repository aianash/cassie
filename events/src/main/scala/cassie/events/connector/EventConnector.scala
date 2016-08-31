package cassie.events.connector

import com.websudos.phantom.connectors.ContactPoint

import cassie.events.EventSettings


class EventConnector(settings: EventSettings) {

  val keySpace = settings.CassandraKeyspace

  lazy val connector = ContactPoint(settings.CassandraHost, settings.CassandraPort).keySpace(keySpace)

}