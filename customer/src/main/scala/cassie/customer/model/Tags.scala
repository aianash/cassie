package cassie.customer.model

import com.websudos.phantom.dsl._

import aianonymous.commons.customer._


class TagsTable extends CassandraTable[Tags, PageTags] {

  override def tableName = "tags"

  // primary key
  object tokenId extends LongColumn(this) with PartitionKey[Long]
  object pageId extends LongColumn(this) with PartitionKey[Long]
  object sectionId extends IntColumn(this) with ClusteringOrder[Int]

  // tags
  object tags extends SetColumn[String](this)

  def fromRow(row: Row) = PageTags(tokenId(row), pageId(row), sectionId(row), tags(row))

}


abstract class Tags extends TagsTable with RootConnector {

  def insertTags(pagetags: PageTags) =
    insert.value(_.tokenId, pagetags.tokenId)
          .value(_.pageId, pagetags.pageId)
          .value(_.sectionId, pagetags.sectionId)
          .value(_.tags, pagetags.tags)

  def getTagsFor(tokenId: Long, pageId: Long) =
    select.where(_.tokenId eqs tokenId)
          .and(_.pageId eqs pageId)

  def deleteTagsFor(tokenId: Long, pageId: Long) =
    delete.where(_.tokenId eqs tokenId)
          .and(_.pageId eqs pageId)

}