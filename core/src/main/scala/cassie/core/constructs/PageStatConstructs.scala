package cassie.core.constructs

import aianash.commons.behavior.Behavior

case class PageStats(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  pageStats     : Behavior.Stats)

case class PageCountStat(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  pageViews     : Long,
  totalVisitors : Long,
  newVisitors   : Long)

case class PageValueStat(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  avgDwellTime  : Long)

case class PageReferral(
  tokenId        : Long,
  pageId         : Long,
  instanceId     : Long,
  refPageId      : Long,
  refCount       : Long)