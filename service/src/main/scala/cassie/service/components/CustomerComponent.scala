package cassie.service.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cassie.customer.CustomerService


case object CustomerComponent extends Component {

  val name = "customer-service"
  val runOnRole = "customer-service"

  def start(system: ActorSystem) = {
    system.actorOf(CustomerService.props, name)
  }

}