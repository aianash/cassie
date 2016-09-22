package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.pagestats.PageStatService


case object PageStatComponent extends Component {

  val name = "pagestats-service"
  val runOnRole = "pagestats-service"

  def start(system: ActorSystem) = {
    system.actorOf(PageStatService.props, name)
  }

}