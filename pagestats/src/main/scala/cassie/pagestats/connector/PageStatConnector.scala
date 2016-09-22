package cassie.pagestats.connector

import com.websudos.phantom.connectors.ContactPoint

import cassie.pagestats.PageStatSettings


class PageStatConnector(settings: PageStatSettings) {

  val keySpace = settings.CassandraKeyspace

  lazy val connector = ContactPoint(settings.CassandraHost, settings.CassandraPort).keySpace(keySpace)

}