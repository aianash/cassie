package cassie.pagestats.model

import com.websudos.phantom.dsl._


case class PageValueStat(
  tokenId       : Long,
  pageId        : Long,
  instanceId    : Long,
  avgDwellTime  : Long
)

sealed class PageValueStatsTable extends CassandraTable[PageValueStats, PageValueStat] {

  override def tableName = "page_value_stats"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object instanceId extends IntColumn(this) with PrimaryKey[Int]

  // data
  object avgDwellTime extends LongColumn(this)

  def fromRow(row: Row) =
    PageValueStat(tokenId(row), pageId(row), instanceId(row), avgDwellTime(row))

}

abstract class PageValueStats extends PageValueStatsTable with RootConnector {

  def updateDwellTime(tokenId: Long, pageId: Long, instanceId: Int, avgDwellTime: Long) =
    update.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .modify(_.avgDwellTime setTo avgDwellTime)

  def getPageValueStats(tokenId: Long, pageId: Long, instanceId: Int) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)

}
