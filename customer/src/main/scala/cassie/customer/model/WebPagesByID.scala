package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.core._
import aianonymous.commons.customer._


class WebPagesByIDTable extends CassandraTable[WebPagesByID, WebPage] {

  override val tableName = "web_pages_by_id"

  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PrimaryKey[Long]

  object url extends StringColumn(this)
  object name extends StringColumn(this)

  def fromRow(row: Row) = WebPage(tokenId(row), pageId(row), PageURL(url(row)), name(row))

}

abstract class WebPagesByID extends WebPagesByIDTable with RootConnector {

  def insertWebPage(webPage: WebPage) =
    insert.value(_.tokenId, webPage.tokenId)
          .value(_.pageId, webPage.pageId)
          .value(_.url, webPage.url.toString)
          .value(_.name, webPage.name)

  def getWebPageFor(tokenId: Long, pageId: Long) =
    select.where(_.tokenId eqs tokenId)
      .and(_.pageId eqs pageId)

}