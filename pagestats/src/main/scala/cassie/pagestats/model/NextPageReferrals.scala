package cassie.pagestats.model

import com.websudos.phantom.dsl._

import cassie.core.constructs._

sealed class NextPageReferralTable extends CassandraTable[NextPageReferrals, PageReferral] {

  override def tableName = "next_page_referrals"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object instanceId extends IntColumn(this) with PartitionKey[Int]
  object nextRefPageId extends LongColumn(this) with PrimaryKey[Long]

  // data
  object refCount extends CounterColumn(this)

  def fromRow(row: Row) =
    PageReferral(tokenId(row), pageId(row), instanceId(row), nextRefPageId(row), refCount(row))

}

abstract class NextPageReferrals extends NextPageReferralTable with RootConnector {

  def updateRefCount(tokenId: Long, pageId: Long, instanceId: Int, nextRefPageId: Long) =
    update.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .and(_.nextRefPageId eqs nextRefPageId)
      .modify(_.refCount += 1)

  def getRefCount(tokenId: Long, pageId: Long, instanceId: Int) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)

}
