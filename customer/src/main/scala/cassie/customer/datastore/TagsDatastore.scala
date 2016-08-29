package cassie.customer.datastore

import scala.concurrent.duration._
import scala.concurrent.Await

import com.websudos.phantom.dsl._

import aianonymous.commons.customer._

import cassie.customer.CustomerSettings
import cassie.customer.connector.CustomerConnector
import cassie.customer.database.TagsDatabase


class TagsDatastore(customerConnector: CustomerConnector) extends TagsDatabase(customerConnector.connector) {

  /**
   * To initialize Tags database
   */
  def init(): Boolean = {
    val creation =
      for {
        _ <- Tags.create.ifNotExists.future()
      } yield true

    Await.result(creation, 2 seconds)
  }

  def insertTags(tags: Seq[PageTags]) = {
    val ids = tags map { tag =>
      (tag.tokenId, tag.pageId)
    } distinct

    val deletebatch =
      ids.foldLeft (Batch.logged) { (b, i) =>
        b.add(Tags.deleteTagsFor(i._1, i._2))
      }

    val addbatch =
      tags.foldLeft (Batch.logged) { (b, i) =>
        b.add(Tags.insertTags(i))
      }

    for {
      _ <- deletebatch.future()
      _ <- addbatch.future()
    } yield true
  }

  def getTagsFor(tokenId: Long, pageId: Long) =
    Tags.getTagsFor(tokenId, pageId).fetch()

}