package cassie.modelparams.database

import com.websudos.phantom.dsl._

import cassie.modelparams.model.Behaviour


class ModelParamDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object Behaviour extends Behaviour with connector.Connector

}