package cassie.events.model

import java.nio.ByteBuffer

import com.websudos.phantom.dsl._

import aianonymous.commons.events._

sealed class EventsTable extends CassandraTable[Events, (Long, Long, Option[TrackingEvent])] {

  override def tableName = "events"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object startTime extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object sessionId extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object eventId extends LongColumn(this) with ClusteringOrder[Long] with Ascending

  // data
  object eventType extends IntColumn(this)
  object eventValue extends BlobColumn(this)
  object eventVersion extends IntColumn(this)

  def fromRow(row: Row) = {
    val sessId = sessionId(row)
    val stTime = startTime(row)

    val etype = eventType(row)
    val evalue = eventValue(row)
    val eversion = eventVersion(row)
    val value = TrackingEvent.decode(evalue.array(), etype.toChar, eversion)

    (sessId, stTime, value)
  }

}

abstract class Events extends EventsTable with RootConnector {

  def insertEvent(tokenId: Long, pageId: Long, startTime: Long, sessionId: Long, eventId: Long, eventType: Int, eventValue: ByteBuffer, eventVersion: Int) =
    insert.value(_.tokenId, tokenId)
      .value(_.pageId, pageId)
      .value(_.startTime, startTime)
      .value(_.sessionId, sessionId)
      .value(_.eventId, eventId)
      .value(_.eventType, eventType)
      .value(_.eventValue, eventValue)
      .value(_.eventVersion, eventVersion)

  def getEventsFor(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.startTime gte startTime)
      .and(_.startTime lte endTime)

}