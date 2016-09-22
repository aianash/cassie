package cassie.core.structures

import aianash.commons.behavior.Behavior

case class PageStats(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  pageStats     : Behavior.Stats
)
