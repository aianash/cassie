package cassie.modelparams.connector

import com.websudos.phantom.connectors.ContactPoint

import cassie.modelparams.ModelParamSettings


class ModelParamConnector(settings: ModelParamSettings) {

  val keySpace = settings.CassandraKeyspace

  lazy val connector = ContactPoint(settings.CassandraHost, settings.CassandraPort).keySpace(keySpace)

}