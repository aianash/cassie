package cassie.pagestats.model

import com.websudos.phantom.dsl._


case class PageCountStat(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  pageViews     : Long,
  totalVisitors : Long,
  newVisitors   : Long
)

sealed class PageCountStatsTable extends CassandraTable[PageCountStats, PageCountStat] {

  override def tableName = "page_count_stats"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object instanceId extends IntColumn(this) with PrimaryKey[Int]

  // data
  object pageViews extends CounterColumn(this)
  object totalVisitors extends CounterColumn(this)
  object newVisitors extends CounterColumn(this)

  def fromRow(row: Row) =
    PageCountStat(tokenId(row), pageId(row), instanceId(row), pageViews(row), totalVisitors(row), newVisitors(row))

}

abstract class PageCountStats extends PageCountStatsTable with RootConnector {

  def updatePageViews(tokenId: Long, pageId: Long, instanceId: Int) =
    update.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .modify(_.pageViews += 1)

  def updateTotalVisitorsAndPageViews(tokenId: Long, pageId: Long, instanceId: Int) =
    update.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .modify(_.pageViews += 1)
      .and(_.totalVisitors += 1)

  def updateAll(tokenId: Long, pageId: Long, instanceId: Int) =
    update.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .modify(_.pageViews += 1)
      .and(_.totalVisitors += 1)
      .and(_.newVisitors += 1)

  def getPageCountStats(tokenId: Long, pageId: Long, instanceId: Int) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)

}
