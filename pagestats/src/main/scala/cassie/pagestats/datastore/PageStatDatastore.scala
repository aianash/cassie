package cassie.pagestats.datastore

import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import com.websudos.phantom.dsl._

import aianash.commons.behavior.Behavior

import cassie.customer.CustomerService

import cassie.pagestats.PageStatSettings
import cassie.pagestats.connector.PageStatConnector
import cassie.pagestats.database.PageStatDatabase


class PageStatDatastore(pageStatConnector: PageStatConnector) extends PageStatDatabase(pageStatConnector.connector) {

  /**
   * To initialize cassandra tables
   */
  def init(): Boolean = {
    val creation =
      for {
        _ <- PageCountStats.create.ifNotExists.future()
        _ <- PageValueStats.create.ifNotExists.future()
        _ <- PrevPageReferrals.create.ifNotExists.future()
        _ <- NextPageReferrals.create.ifNotExists.future()
        _ <- PageVisitors.create.ifNotExists.future()
        _ <- InstanceVisitors.create.ifNotExists.future()
      } yield true

    Await.result(creation, 2 seconds)
  }

  def updatePageVisitStats(tokenId: Long, pageIdTo: Long, instanceId: Int, aianid: Long, pageIdFrom: Option[Long]) = {
    for {
      _ <- updatePageCountStats(tokenId, pageIdTo, instanceId, aianid)
      _ <- updatePageRefCounts(tokenId, pageIdTo, instanceId, pageIdFrom.get) if !pageIdFrom.isEmpty
    } yield true
  }

  def updatePageValueStats(tokenId: Long, pageId: Long, instanceId: Int, avgDwellTime: Long) =
    PageValueStats.updateDwellTime(tokenId, pageId, instanceId, avgDwellTime).future().map(_ => true)

  def getPageStats(tokenId: Long, pageId: Long, instanceId: Int) = {
    for {
      pageCountStatO <- PageCountStats.getPageCountStats(tokenId, pageId, instanceId).one()
      pageValueStatO <- PageValueStats.getPageValueStats(tokenId, pageId, instanceId).one()
      prevPageReferrals <- getPrevPageReferrals(tokenId, pageId, instanceId)
      nextPageReferrals <- getNextPageReferrals(tokenId, pageId, instanceId)
    } yield {
      (pageCountStatO, pageValueStatO, prevPageReferrals, nextPageReferrals)
    }
  }


  private def updatePageCountStats(tokenId: Long, pageId: Long, instanceId: Int, aianid: Long) = {
    (for {
      pvc <- PageVisitors.checkVisitor(tokenId, pageId, aianid).one()
      ivc <- InstanceVisitors.checkVisitor(tokenId, pageId, instanceId, aianid).one() if !pvc.isEmpty
    } yield if(!ivc.isEmpty) {
        PageCountStats.updatePageViews(tokenId, pageId, instanceId).future().map(_ => true)
    } else {
        for {
          _ <- InstanceVisitors.insertVisitor(tokenId, pageId, instanceId, aianid).future()
          _ <- PageCountStats.updateTotalVisitorsAndPageViews(tokenId, pageId, instanceId).future()
        } yield true
    }) recoverWith { case _ =>
      for {
        _ <- PageVisitors.insertVisitor(tokenId, pageId, aianid).future()
        _ <- InstanceVisitors.insertVisitor(tokenId, pageId, instanceId, aianid).future()
        _ <- PageCountStats.updateAll(tokenId, pageId, instanceId).future()
      } yield true
    }
  }

  private def updatePageRefCounts(tokenId: Long, pageIdTo: Long, instanceId: Int, pageIdFrom: Long) = {
    for {
      _ <- PrevPageReferrals.updateRefCount(tokenId, pageIdTo, instanceId, pageIdFrom).future()
      _ <- NextPageReferrals.updateRefCount(tokenId, pageIdFrom, instanceId, pageIdTo).future()
    } yield true
  }

  private def getPrevPageReferrals(tokenId: Long, pageId: Long, instanceId: Int) = {
    PrevPageReferrals.getRefCount(tokenId, pageId, instanceId).fetch()
  }

  private def getNextPageReferrals(tokenId: Long, pageId: Long, instanceId: Int) = {
    NextPageReferrals.getRefCount(tokenId, pageId, instanceId).fetch()
  }

}