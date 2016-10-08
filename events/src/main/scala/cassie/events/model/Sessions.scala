package cassie.events.model

import com.websudos.phantom.dsl._

import aianash.commons.events._


sealed class SessionsTable extends CassandraTable[Sessions, (Long, Long, Long)] {

  override def tableName = "sessions"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object startTime extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object aianId extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object sessionId extends LongColumn(this) with ClusteringOrder[Long] with Ascending

  def fromRow(row: Row) = {
    (aianId(row), sessionId(row), startTime(row))
  }

}

abstract class Sessions extends SessionsTable with RootConnector {

  def insertSession(tokenId: Long, startTime: Long, aianId: Long, sessionId: Long) =
    insert.value(_.tokenId, tokenId)
      .value(_.startTime, startTime)
      .value(_.aianId, aianId)
      .value(_.sessionId, sessionId)

  def getSessionsFor(tokenId: Long, startTime: Long, endTime: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.startTime gte startTime)
      .and(_.startTime lte endTime)

}