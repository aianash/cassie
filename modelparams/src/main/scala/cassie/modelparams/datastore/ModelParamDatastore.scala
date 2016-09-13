package cassie.modelparams.datastore

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import com.websudos.phantom.dsl._

import cassie.modelparams.ModelParamSettings
import cassie.modelparams.connector.ModelParamConnector
import cassie.modelparams.database.ModelParamDatabase


class ModelParamDatastore(modelParamConnector: ModelParamConnector) extends ModelParamDatabase(modelParamConnector.connector) {

  /**
   * To initialize cassandra tables
   */
  def init(): Boolean = {
    val creation =
      for {
        _ <- Behaviour.create.ifNotExists.future()
      } yield true

    Await.result(creation, 2 seconds)
  }

  def insertBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int, alpha: Double, beta: Double) = {
    Behaviour.insertParams(tokenId, pageId, instanceId, alpha, beta).future().map(_ => true)
  }

  def getBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int) = {
    Behaviour.getParams(tokenId, pageId, instanceId).one()
  }

}