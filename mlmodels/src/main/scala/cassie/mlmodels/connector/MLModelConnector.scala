package cassie.mlmodels.connector

import com.websudos.phantom.connectors.ContactPoint

import cassie.mlmodels.MLModelSettings


class MLModelConnector(settings: MLModelSettings) {

  val keySpace = settings.CassandraKeyspace

  lazy val connector = ContactPoint(settings.CassandraHost, settings.CassandraPort).keySpace(keySpace)

}