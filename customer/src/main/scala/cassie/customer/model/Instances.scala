package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.customer.Instance

class InstancesTable extends CassandraTable[Instances, Instance] {

  override def tableName = "instances"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object startHour extends IntColumn(this) with PrimaryKey[Int]

  // data
  object endHour extends IntColumn(this)
  object statOnly extends BooleanColumn(this)
  object name extends StringColumn(this)


  def fromRow(row: Row) =
    Instance(tokenId(row), startHour(row), endHour(row), statOnly(row), name(row))

}


abstract class Instances extends InstancesTable with RootConnector {

  def insertInstance(instance: Instance) =
    insert.value(_.tokenId, instance.tokenId)
          .value(_.startHour, instance.startHour)
          .value(_.endHour, instance.endHour)
          .value(_.statOnly, instance.statOnly)
          .value(_.name, instance.name)

  def getInstance(tokenId: Long) =
    select.where(_.tokenId eqs tokenId)

}