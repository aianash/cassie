package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.customer.PageURL


class PageURLsTable extends CassandraTable[PageURLs, PageURL] {

  override val tableName = "page_url"

  object url extends StringColumn(this) with PartitionKey[String]
  object tokenId extends LongColumn(this)
  object pageId extends LongColumn(this)

  def fromRow(row: Row) =
    PageURL(tokenId(row), pageId(row), url(row))

}

abstract class PageURLs extends PageURLsTable with RootConnector {

  def insertPageUrl(pageUrl: PageURL) =
    insert.value(_.tokenId, pageUrl.tokenId)
          .value(_.pageId, pageUrl.pageId)
          .value(_.url, pageUrl.url)

  def getPageUrlFor(url: String) =
    select.where(_.url eqs url)

}