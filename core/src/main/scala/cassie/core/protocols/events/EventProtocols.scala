package cassie.core.protocols.events

import org.joda.time.DateTime

import aianonymous.commons.core.protocols._, Implicits._

import aianash.commons.events._


sealed trait EventProtocols

case class InsertEvents(eventSession: EventSession, eventVersion: Int) extends EventProtocols with Replyable[Boolean]
case class GetEventSessions(tokenId: TokenId, startTime: DateTime = new DateTime(0L), endTime: DateTime = new DateTime(System.currentTimeMillis())) extends EventProtocols with Replyable[Seq[EventSession]]
case class GetEventCount(tokenId: TokenId, startTime: DateTime = new DateTime(0L), endTime: DateTime = new DateTime(System.currentTimeMillis())) extends EventProtocols with Replyable[Long]
