package cassie.pagestats.model

import com.websudos.phantom.dsl._

sealed class InstanceVisitorsTable extends CassandraTable[InstanceVisitors, Long] {

  override def tableName = "instance_visitors"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object instanceId extends IntColumn(this) with PartitionKey[Int]
  object aianId extends LongColumn(this) with PrimaryKey[Long]

  def fromRow(row: Row) = aianId(row)

}

abstract class InstanceVisitors extends InstanceVisitorsTable with RootConnector {

  def insertVisitor(tokenId: Long, pageId: Long, instanceId: Int, aianId: Long) =
    insert.value(_.tokenId, tokenId)
      .value(_.pageId, pageId)
      .value(_.instanceId, instanceId)
      .value(_.aianId, aianId)

  def checkVisitor(tokenId: Long, pageId: Long, instanceId: Int, aianId: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)
      .and(_.aianId eqs aianId)

}