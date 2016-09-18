package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.core.PageURL
import aianonymous.commons.customer.WebPage


class WebPagesByURLTable extends CassandraTable[WebPagesByURL, WebPage] {

  override val tableName = "web_pages_by_url"

  object url extends StringColumn(this) with PartitionKey[String]
  object tokenId extends LongColumn(this)
  object pageId extends LongColumn(this)
  object name extends StringColumn(this)
  object host extends StringColumn(this)
  object port extends OptionalIntColumn(this)
  object path extends OptionalStringColumn(this)
  object query extends OptionalStringColumn(this)

  def fromRow(row: Row) =
    WebPage(
      tokenId(row),
      pageId(row),
      PageURL(host(row), port(row), path(row), query(row)),
      name(row)
    )

}

abstract class WebPagesByURL extends WebPagesByURLTable with RootConnector {

  def insertWebPage(webPage: WebPage) =
    insert.value(_.url, webPage.url.toString)
          .value(_.tokenId, webPage.tokenId)
          .value(_.pageId, webPage.pageId)
          .value(_.name, webPage.name)
          .value(_.host, webPage.url.host)
          .value(_.port, webPage.url.port)
          .value(_.path, webPage.url.path)
          .value(_.query, webPage.url.query)

  def getWebPageFor(url: PageURL) =
    select.where(_.url eqs url.toString)

}