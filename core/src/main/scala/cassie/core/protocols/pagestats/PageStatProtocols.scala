package cassie.core.protocols.pagestats

import aianonymous.commons.core.protocols._, Implicits._

import cassie.core.structures._


sealed trait PageStatProtocols

case class UpdatePageVisitStats(tokenId: Long, pageIdTo: Long, instanceId: Int, aianid: Long, pageIdFrom: Option[Long]) extends PageStatProtocols with Replyable[Boolean]
case class UpdatePageValueStats(tokenId: Long, pageId: Long, instanceId: Int, avgDwellTime: Long) extends PageStatProtocols with Replyable[Boolean]
case class GetPageStats(tokenId: Long, pageId: Long, instanceId: Int) extends PageStatProtocols with Replyable[PageStats]
