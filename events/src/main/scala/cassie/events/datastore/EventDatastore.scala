package cassie.events.datastore

import java.nio.ByteBuffer

import scala.collection.mutable.{ArrayBuffer, Map}

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import com.websudos.phantom.dsl._

import aianonymous.commons.events._

import cassie.events.EventSettings
import cassie.events.connector.EventConnector
import cassie.events.database.EventDatabase


class EventDatastore(eventConnector: EventConnector) extends EventDatabase(eventConnector.connector) {

  /**
   * To initialize cassandra tables
   */
  def init(): Boolean = {
    val creation =
      for {
        _ <- Sessions.create.ifNotExists.future()
        _ <- Events.create.ifNotExists.future()
      } yield true

    Await.result(creation, 2 seconds)
  }

  def insertEvents(eventsSession: EventsSession, eventVersion: Int): Future[Boolean] = {
    val tokenId = eventsSession.tokenId
    val aianId = eventsSession.aianId
    val sessionId = eventsSession.sessionId

    var insertStatus = ArrayBuffer.empty[Future[Boolean]]

    for(pageEvent <- eventsSession.pageEvents) {
      val pageId = pageEvent.pageId
      val startTime = pageEvent.startTime

      val batch = pageEvent.events.flatMap(event => createInsertEvent(eventsSession, pageEvent, event, eventVersion))
                                  .foldLeft(Batch.logged)(_ add _)
      insertStatus +=
        (for {
          _ <- Sessions.insertSession(tokenId, pageId, startTime, sessionId, aianId).future()
          _ <- batch.future()
        } yield true) recover {
          case ex: Exception => false
        }
    }

    Future.sequence(insertStatus).map(_.foldLeft(true)(_ && _))
  }

  def getEvents(tokenId: Long, pageId: Long, startTime: Long, endTime: Long): Future[Seq[PageEvents]] = {
    Events.getEventsFor(tokenId, pageId, startTime, endTime).fetch().flatMap { eventTupleList =>
      if(!eventTupleList.isEmpty) {
        val pageEvents = eventTupleList.foldLeft(Map.empty[(Long, Long), ArrayBuffer[TrackingEvent]]) {
          case (m, (sid, sttime, Some(value))) =>
            m.getOrElseUpdate((sid, sttime), ArrayBuffer.empty[TrackingEvent]) += value
            m
          case (m, _) => m
        }.foldLeft(ArrayBuffer.empty[PageEvents]) {
          case (pge, ((sid, sttime), value)) =>
            pge += PageEvents(sid, pageId, sttime, value)
        }

        Future.successful(pageEvents)
      } else {
        Future.successful(Seq.empty[PageEvents])
      }
    }
  }

  def getEventsCount(tokenId: Long, pageId: Long, startTime: Long, endTime: Long): Future[Long] =
    Events.getEventsCountFor(tokenId, pageId, startTime, endTime).one().map(_.getOrElse(0L))

  private def createInsertEvent(session: EventsSession, pgevents: PageEvents, event: TrackingEvent, eventVersion: Int) =
    (event match {
      case pfv: PageFragmentView =>
        val eventId = pfv.startTime
        val eventType = TrackingEvent.codeFor(pfv)
        TrackingEvent.encode(pfv, eventVersion).map(v => (eventId, eventType, v))
      case sv: SectionView =>
        val eventId = sv.startTime
        val eventType = TrackingEvent.codeFor(sv)
        TrackingEvent.encode(sv, eventVersion).map(v => (eventId, eventType, v))
      case mp: MousePath =>
        val eventId = mp.startTime
        val eventType = TrackingEvent.codeFor(mp)
        TrackingEvent.encode(mp, eventVersion).map(v => (eventId, eventType, v))
      case sc: Scanning =>
        val eventId = sc.startTime
        val eventType = TrackingEvent.codeFor(sc)
        TrackingEvent.encode(sc, eventVersion).map(v => (eventId, eventType, v))
      case _ => None
    }).map {
      case (eventId, eventType, eventValue) =>
        import pgevents._
        Events.insertEvent(session.tokenId, pageId, startTime, sessionId, eventId, eventType.toInt,
          ByteBuffer.wrap(eventValue), eventVersion)
    }

}