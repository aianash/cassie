package cassie.events.model

import com.websudos.phantom.dsl._

import aianash.commons.events._


sealed class SessionsTable extends CassandraTable[Sessions, (Long, Long, Long)] {

  override def tableName = "sessions"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object aianId extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object sessionId extends LongColumn(this) with ClusteringOrder[Long] with Ascending

  // data
  object startTime extends LongColumn(this)

  def fromRow(row: Row) = {
    (aianId(row), sessionId(row), startTime(row))
  }

}

abstract class Sessions extends SessionsTable with RootConnector {

  def insertSession(tokenId: Long, aianId: Long, sessionId: Long, startTime: Long) =
    insert.value(_.tokenId, tokenId)
      .value(_.aianId, aianId)
      .value(_.sessionId, sessionId)
      .value(_.startTime, startTime)

  def getSessionsFor(tokenId: Long) =
    select.where(_.tokenId eqs tokenId)

}