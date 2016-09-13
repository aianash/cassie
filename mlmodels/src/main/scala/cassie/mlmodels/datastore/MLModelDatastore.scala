package cassie.mlmodels.datastore

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import com.websudos.phantom.dsl._

import cassie.mlmodels.MLModelSettings
import cassie.mlmodels.connector.MLModelConnector
import cassie.mlmodels.database.MLModelDatabase


class MLModelDatastore(mlModelConnector: MLModelConnector) extends MLModelDatabase(mlModelConnector.connector) {

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