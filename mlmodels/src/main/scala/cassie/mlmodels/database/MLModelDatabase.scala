package cassie.mlmodels.database

import com.websudos.phantom.dsl._

import cassie.mlmodels.model.Behaviour


class MLModelDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object Behaviour extends Behaviour with connector.Connector

}