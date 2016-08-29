package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.customer.Domain


class DomainsTable extends CassandraTable[Domains, Domain] {

  override val tableName = "domains"

  object domain extends StringColumn(this) with PartitionKey[String]
  object tokenId extends LongColumn(this)

  def fromRow(row: Row) =
    Domain(tokenId(row), domain(row))

}

abstract class Domains extends DomainsTable with RootConnector {

  def insertDomain(domain: Domain) =
    insert.value(_.domain, domain.name)
          .value(_.tokenId, domain.tokenId)

  def getDomainFor(name: String) =
    select.where(_.domain eqs name)

}