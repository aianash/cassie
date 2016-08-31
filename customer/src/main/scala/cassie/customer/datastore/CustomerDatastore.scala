package cassie.customer.datastore

import scala.concurrent.duration._
import scala.concurrent.Await

import com.websudos.phantom.dsl._

import aianonymous.commons.customer._

import cassie.customer.CustomerSettings
import cassie.customer.connector.CustomerConnector
import cassie.customer.database.CustomerDatabase


class CustomerDatastore(customerConnector: CustomerConnector) extends CustomerDatabase(customerConnector.connector) {

  /**
   * To initialize Tags database
   */
  def init(): Boolean = {
    val creation =
      for {
        _ <- Tags.create.ifNotExists.future()
        _ <- Domains.create.ifNotExists.future()
        _ <- PageURLs.create.ifNotExists.future()
      } yield true

    Await.result(creation, 2 seconds)
  }

  /////////////////////////////////////////////////////////////////////
  ///////////////////////////// Page Tags /////////////////////////////
  /////////////////////////////////////////////////////////////////////

  def insertTags(tags: Seq[PageTags]) = {
    val ids = tags.map(x => (x.tokenId, x.pageId)).distinct

    val deletebatch =
      ids.foldLeft (Batch.logged) { case (batch, (tokenId, pageId)) =>
        batch add Tags.deleteTagsFor(tokenId, pageId)
      }

    val addbatch =
      tags.filter(!_.tags.isEmpty)
          .foldLeft(Batch.logged) { (b, i) => b add Tags.insertTags(i) }

    for {
      _ <- deletebatch.future()
      _ <- addbatch.future()
    } yield true
  }

  def getTagsFor(tokenId: Long, pageId: Long) =
    Tags.getTagsFor(tokenId, pageId).fetch()

  /////////////////////////////////////////////////////////////////////
  ////////////////////////////// Domains //////////////////////////////
  /////////////////////////////////////////////////////////////////////

  def insertDomain(domain: Domain) =
    Domains.insertDomain(domain).future().map(_ => true)

  def getDomainFor(name: String) =
    Domains.getDomainFor(name).one()

  /////////////////////////////////////////////////////////////////////
  ///////////////////////////// Page URLs /////////////////////////////
  /////////////////////////////////////////////////////////////////////

  def insertPageUrl(pageUrl: PageURL) =
    PageURLs.insertPageUrl(pageUrl).future().map(_ => true)

  def getPageUrl(url: String) =
    PageURLs.getPageUrlFor(url).one()

}