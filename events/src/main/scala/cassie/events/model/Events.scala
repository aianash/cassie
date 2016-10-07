package cassie.events.model

import java.nio.ByteBuffer

import com.websudos.phantom.dsl._

import aianash.commons.events._


sealed class EventsTable extends CassandraTable[Events, (Long, Long, Option[TrackingEvent])] {

  override def tableName = "events"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object aianId extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object sessionId extends LongColumn(this) with ClusteringOrder[Long] with Ascending
  object eventId extends LongColumn(this) with ClusteringOrder[Long] with Ascending

  // data
  object eventType extends IntColumn(this)
  object eventVersion extends IntColumn(this)
  object eventValue extends BlobColumn(this)

  def fromRow(row: Row) = {
    val evType = eventType(row)
    val evVersion = eventVersion(row)
    val evValue = eventValue(row)
    val value = TrackingEvent.decode(evValue.array(), evType.toChar, evVersion)

    (aianId(row), sessionId(row), value)
  }

}

abstract class Events extends EventsTable with RootConnector {

  def insertEvent(tokenId: Long, aianId: Long, sessionId: Long, eventId: Long,
    eventType: Int, eventVersion: Int, eventValue: ByteBuffer) =
    insert.value(_.tokenId, tokenId)
      .value(_.aianId, aianId)
      .value(_.sessionId, sessionId)
      .value(_.eventId, eventId)
      .value(_.eventType, eventType)
      .value(_.eventVersion, eventVersion)
      .value(_.eventValue, eventValue)

  def getEventsFor(tokenId: Long) =
    select.where(_.tokenId eqs tokenId)

  def getEventsCountFor(tokenId: Long) =
    select.count.where(_.tokenId eqs tokenId)

}