package cassie.events

import akka.actor.{ActorSystem, ExtendedActorSystem}
import akka.actor.{Extension, ExtensionId, ExtensionIdProvider}

import com.typesafe.config.{Config, ConfigFactory}

class EventSettings(cfg: Config) extends Extension {

  final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cassie.events")
    config
  }

  val CassandraHost     = config.getString("cassie.events.cassandraHost")
  val CassandraPort     = config.getInt("cassie.events.cassandraPort")
  val CassandraKeyspace = config.getString("cassie.events.cassandraKeyspace")
  val ServiceId         = config.getLong("cassie.service.id")
  val DatacenterId      = config.getLong("cassie.datacenter.id")
}

object EventSettings extends ExtensionId[EventSettings] with ExtensionIdProvider {

  override def lookup = EventSettings

  override def createExtension(system: ExtendedActorSystem) =
    new EventSettings(system.settings.config)

  override def get(system: ActorSystem) = super.get(system)
}