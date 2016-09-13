package cassie.mlmodels

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import cassie.core.protocols.mlmodels._

import cassie.mlmodels.datastore.MLModelDatastore
import cassie.mlmodels.connector.MLModelConnector


class MLModelService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = MLModelSettings(context.system)
  private val connector = new MLModelConnector(settings)
  private val datastore = new MLModelDatastore(connector)
  datastore.init()

  def receive = {

    case InsertBehaviourParams(tokenId, pageId, instanceId, alpha, beta) =>
      datastore.insertBehaviourParams(tokenId, pageId, instanceId, alpha, beta) pipeTo sender()

    case GetBehaviourParams(tokenId, pageId, instanceId) =>
      datastore.getBehaviourParams(tokenId, pageId, instanceId) pipeTo sender()

  }

}

object MLModelService {

  def props = Props(classOf[MLModelService])

}
