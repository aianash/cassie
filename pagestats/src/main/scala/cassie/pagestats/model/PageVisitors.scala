package cassie.pagestats.model

import com.websudos.phantom.dsl._

sealed class PageVisitorsTable extends CassandraTable[PageVisitors, Long] {

  override def tableName = "page_visitors"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object aianId extends LongColumn(this) with PrimaryKey[Long]

  def fromRow(row: Row) = aianId(row)

}

abstract class PageVisitors extends PageVisitorsTable with RootConnector {

  def insertVisitor(tokenId: Long, pageId: Long, aianId: Long) =
    insert.value(_.tokenId, tokenId)
      .value(_.pageId, pageId)
      .value(_.aianId, aianId)

  def checkVisitor(tokenId: Long, pageId: Long, aianId: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.aianId eqs aianId)

}