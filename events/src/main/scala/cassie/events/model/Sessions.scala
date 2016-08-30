package cassie.events.model

import com.websudos.phantom.dsl._

import aianonymous.commons.events._


sealed class SessionsTable extends CassandraTable[Sessions, (Long, Long)] {

  override def tableName = "sessions"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object startTime extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object sessionId extends LongColumn(this) with ClusteringOrder[Long] with Ascending

  // data
  object aianId extends LongColumn(this)

  def fromRow(row: Row) = {
    (sessionId(row), aianId(row))
  }

}

abstract class Sessions extends SessionsTable with RootConnector {

  def insertSession(tokenId: Long, pageId: Long, startTime: Long, sessionId: Long, aianId: Long) =
    insert.value(_.tokenId, tokenId)
      .value(_.pageId, pageId)
      .value(_.startTime, startTime)
      .value(_.sessionId, sessionId)
      .value(_.aianId, aianId)

  def getSessionsFor(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.startTime gte startTime)
      .and(_.startTime lte endTime)
}