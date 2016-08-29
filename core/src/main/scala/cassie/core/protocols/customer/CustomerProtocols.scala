package cassie.core.protocols.customer

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.customer._


sealed trait CustomerProtocols

case class InsertPageTags(tags: Seq[PageTags]) extends CustomerProtocols with Replyable[Boolean]