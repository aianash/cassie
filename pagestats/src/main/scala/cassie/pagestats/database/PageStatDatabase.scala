package cassie.pagestats.database

import com.websudos.phantom.dsl._

import cassie.pagestats.model._


class PageStatDatabase(override val connector: KeySpaceDef) extends Database(connector) {

  object PageCountStats extends PageCountStats with connector.Connector
  object PageValueStats extends PageValueStats with connector.Connector
  object PrevPageReferrals extends PrevPageReferrals with connector.Connector
  object NextPageReferrals extends NextPageReferrals with connector.Connector
  object PageVisitors extends PageVisitors with connector.Connector
  object InstanceVisitors extends InstanceVisitors with connector.Connector

}