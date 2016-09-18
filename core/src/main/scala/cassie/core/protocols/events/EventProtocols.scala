package cassie.core.protocols.events

import aianonymous.commons.core.protocols._, Implicits._

import aianonymous.commons.events._


sealed trait EventProtocol

case class InsertEvents(eventsSession: EventsSession, eventVersion: Int) extends EventProtocol with Replyable[Boolean]
case class GetEvents(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends EventProtocol with Replyable[Seq[PageEvents]]
case class GetEventsCount(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends EventProtocol with Replyable[Long]
case class InsertSession(tokenId: Long, pageId: Long, startTime: Long, sessionId: Long, aianId: Long) extends EventProtocol with Replyable[Boolean]