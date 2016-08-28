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

  def insertTags(tags: PageTags) = Tags.insertTags(tags).future().map(_ => true)

}