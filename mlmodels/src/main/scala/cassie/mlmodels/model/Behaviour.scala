package cassie.mlmodels.model

import com.websudos.phantom.dsl._

sealed class BehaviourTable extends CassandraTable[Behaviour, (Double, Double)] {

  override def tableName = "behaviour"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object instanceId extends IntColumn(this) with ClusteringOrder[Int] with Ascending

  // data
  object alpha extends DoubleColumn(this)
  object beta extends DoubleColumn(this)

  def fromRow(row: Row) = {
    (alpha(row), beta(row))
  }

}

abstract class Behaviour extends BehaviourTable with RootConnector {

  def insertParams(tokenId: Long, pageId: Long, instanceId: Int, alpha: Double, beta: Double) =
    insert.value(_.tokenId, tokenId)
      .value(_.pageId, pageId)
      .value(_.instanceId, instanceId)
      .value(_.alpha, alpha)
      .value(_.beta, beta)

  def getParams(tokenId: Long, pageId: Long, instanceId: Int) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)
      .and(_.instanceId eqs instanceId)

}