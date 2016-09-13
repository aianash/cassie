package cassie.core.protocols.modelparams

import aianonymous.commons.core.protocols._, Implicits._


sealed trait ModelParamProtocols

case class InsertBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int, alpha: Double, beta: Double) extends ModelParamProtocols with Replyable[Boolean]
case class GetBehaviourParams(tokenId: Long, pageId: Long, instanceId: Int) extends ModelParamProtocols with Replyable[Option[(Double, Double)]]
