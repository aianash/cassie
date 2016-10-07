package cassie.events.datastore

import java.nio.ByteBuffer

import scala.collection.mutable.{ArrayBuffer, Map}

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import org.joda.time.DateTime

import com.websudos.phantom.dsl._

import aianash.commons.events._

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

  def insertEvents(eventSession: EventSession, eventVersion: Int): Future[Boolean] = {
    val tokenId = eventSession.tokenId
    val aianId = eventSession.aianId
    val sessionId = eventSession.sessionId
    val startTime = eventSession.startTime

    val batch = eventSession.events.flatMap(event => createInsertEvent(eventSession, event, eventVersion))
                                   .foldLeft(Batch.logged)(_ add _)
    for {
      _ <- Sessions.insertSession(tokenId.tkuuid, aianId.anuuid, sessionId.snuuid, startTime.getMillis).future()
      _ <- batch.future()
    } yield true
  }

  def getEvents(tokenId: Long): Future[Seq[EventSession]] = {
    for {
      sessionList <- Sessions.getSessionsFor(tokenId).fetch()
      eventList   <- Events.getEventsFor(tokenId).fetch() if !sessionList.isEmpty
    } yield if(!eventList.isEmpty) {
        val eventMap = eventList.foldLeft(Map.empty[(Long, Long), ArrayBuffer[TrackingEvent]]) {
          case (m, (aianId, sessionId, Some(eventValue))) =>
            m.getOrElseUpdate((aianId, sessionId), ArrayBuffer.empty[TrackingEvent]) += eventValue
            m
          case (m, _) => m
        }

        val sessionMap = sessionList.foldLeft(Map.empty[(Long, Long), Long]) {
          case (m, (aianId, sessionId, startTime)) =>
            m.put((aianId, sessionId), startTime)
            m
          case (m, _) => m
        }

        val eventSessions = eventMap.foldLeft(ArrayBuffer.empty[EventSession]) {
          case (evs, ((aianId, sessionId), events)) =>
            val startTimeO = sessionMap.get((aianId, sessionId))
            startTimeO match {
              case Some(startTime) =>
                evs += EventSession(TokenId(tokenId), AianId(aianId), SessionId(sessionId),
                  new DateTime(startTime), events)
              case _ => evs
            }
        }
        eventSessions
    } else {
      Seq.empty[EventSession]
    }
  }

  def getEventsCount(tokenId: Long): Future[Long] =
    Events.getEventsCountFor(tokenId).one().map(_.getOrElse(0L))


  private def createInsertEvent(session: EventSession, event: TrackingEvent, eventVersion: Int) =
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
      case ac: Action =>
        val eventId = ac.timeStamp
        val eventType = TrackingEvent.codeFor(ac)
        TrackingEvent.encode(ac, eventVersion).map(v => (eventId, eventType, v))
      case _ => None
    }).map {
      case (eventId, eventType, eventValue) =>
        import session._
        Events.insertEvent(tokenId.tkuuid, aianId.anuuid, sessionId.snuuid, eventId.getMillis, eventType.toInt,
          eventVersion, ByteBuffer.wrap(eventValue))
    }

}