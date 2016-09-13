package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.modelparams.ModelParamService


case object ModelParamComponent extends Component {

  val name = "modelparam-service"
  val runOnRole = "modelparam-service"

  def start(system: ActorSystem) = {
    system.actorOf(ModelParamService.props, name)
  }

}