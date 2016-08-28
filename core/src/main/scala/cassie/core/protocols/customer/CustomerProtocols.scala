package cassie.core.protocols.customer

import aianonymous.commons.customer._


sealed trait CustomerProtocols

case class InsertTags(tags: PageTags) extends CustomerProtocols