package cassie.modelparams

import akka.actor.{ActorSystem, ExtendedActorSystem}
import akka.actor.{Extension, ExtensionId, ExtensionIdProvider}

import com.typesafe.config.{Config, ConfigFactory}

class ModelParamSettings(cfg: Config) extends Extension {

  final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cassie.modelparams")
    config
  }

  val CassandraHost     = config.getString("cassie.modelparams.cassandraHost")
  val CassandraPort     = config.getInt("cassie.modelparams.cassandraPort")
  val CassandraKeyspace = config.getString("cassie.modelparams.cassandraKeyspace")
  val ServiceId         = config.getLong("cassie.service.id")
  val DatacenterId      = config.getLong("cassie.datacenter.id")
}

object ModelParamSettings extends ExtensionId[ModelParamSettings] with ExtensionIdProvider {

  override def lookup = ModelParamSettings

  override def createExtension(system: ExtendedActorSystem) =
    new ModelParamSettings(system.settings.config)

  override def get(system: ActorSystem) = super.get(system)
}