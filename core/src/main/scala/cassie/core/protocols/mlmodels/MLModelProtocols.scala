package cassie.core.protocols.mlmodels

import aianonymous.commons.core.protocols._, Implicits._


sealed trait MLModelProtocols

case class InsertBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int, alpha: Double, beta: Double) extends MLModelProtocols with Replyable[Boolean]
case class GetBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int) extends MLModelProtocols with Replyable[Option[(Double, Double)]]
