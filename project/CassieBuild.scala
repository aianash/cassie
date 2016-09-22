import sbt._
import sbt.Classpaths.publishTask
import Keys._

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import sbtassembly.AssemblyPlugin.autoImport._

import com.typesafe.sbt.SbtNativePackager._, autoImport._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

import com.aianonymous.sbt.standard.libraries.StandardLibraries

import com.typesafe.sbt.packager.docker.DockerPlugin


object CassieBuild extends Build with StandardLibraries {

  lazy val makeScript = TaskKey[Unit]("make-script", "make bash script in local directory to run main classes")

  def sharedSettings = Seq(
    organization := "com.aianonymous",
    version := "0.1.0",
    scalaVersion := Version.scala,
    crossScalaVersions := Seq(Version.scala, "2.10.6"),
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps", "-language:reflectiveCalls", "-Yinline-warnings", "-encoding", "utf8"),
    retrieveManaged := true,

    fork := true,
    javaOptions += "-Xmx2500M",

    resolvers ++= StandardResolvers,

    publishMavenStyle := true
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

  lazy val cassie = Project(
    id = "cassie",
    base = file("."),
    settings = Project.defaultSettings
  ).aggregate(core, customer, service, events, modelparams, pagestats)


  lazy val core = Project(
    id = "cassie-core",
    base = file("core"),
    settings = Project.defaultSettings
      ++ sharedSettings
  ).settings(
    name := "cassie-core",
    libraryDependencies ++= Seq(
    ) ++ Libs.commonsCore
      ++ Libs.commonsCustomer
      ++ Libs.commonsEvents
      ++ Libs.commonsBehavior
  )


  lazy val customer = Project(
    id = "cassie-customer",
    base = file("customer"),
    settings = Project.defaultSettings
      ++ sharedSettings
  )
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-customer",
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
      ++ Libs.microservice
      ++ Libs.phantom
      ++ Libs.commonsCustomer
  ).dependsOn(core)


  lazy val service = Project(
    id = "cassie-service",
    base = file("service"),
    settings = Project.defaultSettings
      ++ sharedSettings
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "cassie-service",
    mainClass in Compile := Some("cassie.service.CassieService"),
    dockerExposedPorts := Seq(4848),
    dockerEntrypoint := Seq("sh", "-c",
                            """export CASSIE_HOST=`ifdata -pa eth0` && echo $CASSIE_HOST && \
                            |  export CASSIE_PORT=4848 && \
                            |  bin/cassie-service -Dakka.cluster.roles.0=customer-service -Dakka.cluster.roles.1=event-service -Dakka.cluster.roles.2=modelparams-service -Dakka.cluster.roles.3=pagestats-service$*""".stripMargin
                            ),
    dockerRepository := Some("aianonymous"),
    dockerBaseImage := "aianonymous/baseimage",
    dockerCommands ++= Seq(
      Cmd("USER", "root")
    ),
    libraryDependencies ++= Seq(
    ) ++ Libs.microservice
      ++ Libs.commonsEvents
      ++ Libs.microservice,
    makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
      var path = dir / "bin" / "cassie-service"
      sbt.Process(Seq("ln", "-sf", path.toString, "cassie-service"), cwd) ! streams.log
    }
  ).dependsOn(customer, events, modelparams, pagestats)


  lazy val events = Project(
    id = "cassie-events",
    base = file("events"),
    settings = Project.defaultSettings ++
      sharedSettings
    )
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-events",
    libraryDependencies ++= Seq(
    ) ++ Libs.scalaz
      ++ Libs.akka
      ++ Libs.msgpack
      ++ Libs.commonsEvents,
    makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
    var path = dir / "bin" / "cassie-events"
    sbt.Process(Seq("ln", "-sf", path.toString, "cassie-events"), cwd) ! streams.log
    }
  ).dependsOn(core)


  lazy val modelparams = Project(
    id = "cassie-modelparams",
    base = file("modelparams"),
    settings = Project.defaultSettings ++
      sharedSettings
    )
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-modelparams",
    libraryDependencies ++= Seq(
    ) ++ Libs.scalaz
      ++ Libs.akka,
    makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
    var path = dir / "bin" / "cassie-modelparams"
    sbt.Process(Seq("ln", "-sf", path.toString, "cassie-modelparams"), cwd) ! streams.log
    }
  ).dependsOn(core)

  lazy val pagestats = Project(
    id = "cassie-pagestats",
    base = file("pagestats"),
    settings = Project.defaultSettings ++
      sharedSettings
    )
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-pagestats",
    libraryDependencies ++= Seq(
    ) ++ Libs.scalaz
      ++ Libs.akka
      ++ Libs.commonsBehavior,
    makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
    var path = dir / "bin" / "cassie-pagestats"
    sbt.Process(Seq("ln", "-sf", path.toString, "cassie-pagestats"), cwd) ! streams.log
    }
  ).dependsOn(core, customer)

}