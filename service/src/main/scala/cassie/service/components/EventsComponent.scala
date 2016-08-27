// package cassie.service.components

// import akka.actor.ActorSystem

// import aianonymous.commons.microservice.Component


// case object EventComponent extends Component {
//   val name = "event-service"
//   val runOnRole = "event-service"

//   def start(system: ActorSystem) = {
//     system.actorOf(EventService.props, name)
//   }
// }