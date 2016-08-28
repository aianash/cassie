package cassie.customer

import akka.actor.{ActorSystem, ExtendedActorSystem}
import akka.actor.{Extension, ExtensionId, ExtensionIdProvider}

import com.typesafe.config.{Config, ConfigFactory}


class CustomerSettings(cfg: Config) extends Extension {

  final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cassie.customer")
    config
  }

  val CassandraHost     = config.getString("cassie.customer.cassandraHost")
  val CassandraPort     = config.getInt("cassie.customer.cassandraPort")
  val CassandraKeyspace = config.getString("cassie.customer.cassandraKeyspace")
  val ServiceId         = config.getLong("cassie.service.id")
  val DatacenterId      = config.getLong("cassie.datacenter.id")

}


object CustomerSettings extends ExtensionId[CustomerSettings] with ExtensionIdProvider {

  override def lookup = CustomerSettings

  override def createExtension(system: ExtendedActorSystem) =
    new CustomerSettings(system.settings.config)

  override def get(system: ActorSystem) = super.get(system)

}