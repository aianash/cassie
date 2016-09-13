package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.mlmodels.MLModelService


case object MLModelComponent extends Component {

  val name = "mlmodel-service"
  val runOnRole = "mlmodel-service"

  def start(system: ActorSystem) = {
    system.actorOf(MLModelService.props, name)
  }

}