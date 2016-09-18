package cassie.core.protocols.customer

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.core.PageURL
import aianonymous.commons.customer._


sealed trait CustomerProtocol

case class InsertPageTags(tags: Seq[PageTags]) extends CustomerProtocol with Replyable[Boolean]
case class FetchPageTags(tid: Long, pid: Long) extends CustomerProtocol with Replyable[Seq[PageTags]]

case class GetDomain(name: String) extends CustomerProtocol with Replyable[Option[Domain]]
case class GetOrCreatePageId(url: PageURL, tokenId: Long, name: String) extends CustomerProtocol with Replyable[Long]
case class GetPageId(url: PageURL) extends CustomerProtocol with Replyable[Option[Long]]
case class GetWebPage(url: PageURL) extends CustomerProtocol with Replyable[Option[WebPage]]