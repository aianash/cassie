package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.modelparams.ModelParamService


case object ModelParamComponent extends Component {

  val name = "modelparams-service"
  val runOnRole = "modelparams-service"

  def start(system: ActorSystem) = {
    system.actorOf(ModelParamService.props, name)
  }

}