package cassie.pagestats

import akka.actor.{ActorSystem, ExtendedActorSystem}
import akka.actor.{Extension, ExtensionId, ExtensionIdProvider}

import com.typesafe.config.{Config, ConfigFactory}

class PageStatSettings(cfg: Config) extends Extension {

  final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cassie.pagestats")
    config
  }

  val CassandraHost     = config.getString("cassie.pagestats.cassandraHost")
  val CassandraPort     = config.getInt("cassie.pagestats.cassandraPort")
  val CassandraKeyspace = config.getString("cassie.pagestats.cassandraKeyspace")
  val ServiceId         = config.getLong("cassie.service.id")
  val DatacenterId      = config.getLong("cassie.datacenter.id")
}

object PageStatSettings extends ExtensionId[PageStatSettings] with ExtensionIdProvider {

  override def lookup = PageStatSettings

  override def createExtension(system: ExtendedActorSystem) =
    new PageStatSettings(system.settings.config)

  override def get(system: ActorSystem) = super.get(system)
}