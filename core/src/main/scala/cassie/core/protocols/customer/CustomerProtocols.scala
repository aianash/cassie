package cassie.core.protocols.customer

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.customer._


sealed trait CustomerProtocol

case class InsertPageTags(tags: Seq[PageTags]) extends CustomerProtocol with Replyable[Boolean]
case class FetchPageTags(tid: Long, pid: Long) extends CustomerProtocol with Replyable[Seq[PageTags]]

case class GetDomain(name: String) extends CustomerProtocol with Replyable[Option[Domain]]
case class GetPageURL(url: String, tokenId: Long) extends CustomerProtocol with Replyable[PageURL]