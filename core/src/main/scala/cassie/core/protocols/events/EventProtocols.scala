package cassie.core.protocols.events

import aianonymous.commons.core.protocols._, Implicits._

import aianash.commons.events._


sealed trait EventProtocols

case class InsertEvents(eventSession: EventSession, eventVersion: Int) extends EventProtocols with Replyable[Boolean]
case class GetEvents(tokenId: TokenId) extends EventProtocols with Replyable[Seq[EventSession]]
case class GetEventsCount(tokenId: TokenId) extends EventProtocols with Replyable[Long]
