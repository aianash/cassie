package cassie.mlmodels

import akka.actor.{ActorSystem, ExtendedActorSystem}
import akka.actor.{Extension, ExtensionId, ExtensionIdProvider}

import com.typesafe.config.{Config, ConfigFactory}

class MLModelSettings(cfg: Config) extends Extension {

  final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cassie.mlmodels")
    config
  }

  val CassandraHost     = config.getString("cassie.mlmodels.cassandraHost")
  val CassandraPort     = config.getInt("cassie.mlmodels.cassandraPort")
  val CassandraKeyspace = config.getString("cassie.mlmodels.cassandraKeyspace")
  val ServiceId         = config.getLong("cassie.service.id")
  val DatacenterId      = config.getLong("cassie.datacenter.id")
}

object MLModelSettings extends ExtensionId[MLModelSettings] with ExtensionIdProvider {

  override def lookup = MLModelSettings

  override def createExtension(system: ExtendedActorSystem) =
    new MLModelSettings(system.settings.config)

  override def get(system: ActorSystem) = super.get(system)
}