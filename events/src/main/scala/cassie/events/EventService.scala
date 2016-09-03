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

    case GetEvents(tokenId, pageId, startTime, endTime) =>
      datastore.getEvents(tokenId, pageId, startTime, endTime) pipeTo sender()

    case GetEventsCount(tokenId, pageId, startTime, endTime) =>
      datastore.getEventsCount(tokenId, pageId, startTime, endTime) pipeTo sender()

  }

}

object EventService {

  def props = Props(classOf[EventService])

}
