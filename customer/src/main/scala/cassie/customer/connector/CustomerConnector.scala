package cassie.customer.connector

import com.websudos.phantom.connectors.ContactPoint

import cassie.customer.CustomerSettings


class CustomerConnector(settings: CustomerSettings) {

  val keyspace = settings.CassandraKeyspace

  lazy val connector =
    ContactPoint(settings.CassandraHost, settings.CassandraPort).keySpace(keyspace)

}