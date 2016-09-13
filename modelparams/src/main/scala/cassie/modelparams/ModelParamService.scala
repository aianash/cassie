package cassie.modelparams

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import cassie.core.protocols.modelparams._

import cassie.modelparams.datastore.ModelParamDatastore
import cassie.modelparams.connector.ModelParamConnector


class ModelParamService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = ModelParamSettings(context.system)
  private val connector = new ModelParamConnector(settings)
  private val datastore = new ModelParamDatastore(connector)
  datastore.init()

  def receive = {

    case InsertBehaviourParams(tokenId, pageId, instanceId, alpha, beta) =>
      datastore.insertBehaviourParams(tokenId, pageId, instanceId, alpha, beta) pipeTo sender()

    case GetBehaviourParams(tokenId, pageId, instanceId) =>
      datastore.getBehaviourParams(tokenId, pageId, instanceId) pipeTo sender()

  }

}

object ModelParamService {

  def props = Props(classOf[ModelParamService])

}
