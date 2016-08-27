package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.test._


case object TestComponent extends Component {
  val name = "test-service"
  val runOnRole = "test-service"

  def start(system: ActorSystem) = {
    system.actorOf(TestActor.props, name)
  }
}