package cassie.events

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import cassie.core.protocols.events._

import cassie.events.datastore.EventDatastore
import cassie.events.connector.EventConnector


class EventService extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = EventSettings(context.system)
  private val connector = new EventConnector(settings)
  private val datastore = new EventDatastore(connector)
  datastore.init()

  def receive = {

    case InsertEvents(eventsSession, eventVersion) =>
      datastore.insertEvents(eventsSession, eventVersion) pipeTo sender()

    case GetEventSessions(tokenId, startTime, endTime) =>
      datastore.getEventSessions(tokenId.tkuuid, startTime.getMillis, endTime.getMillis) pipeTo sender()

    case GetEventCount(tokenId, startTime, endTime) =>
      datastore.getEventCount(tokenId.tkuuid, startTime.getMillis, endTime.getMillis) pipeTo sender()

  }

}

object EventService {

  def props = Props(classOf[EventService])

}
