import sbt._
import sbt.Classpaths.publishTask
import Keys._

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import sbtassembly.AssemblyPlugin.autoImport._

import com.typesafe.sbt.SbtNativePackager._, autoImport._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

import com.aianonymous.sbt.standard.libraries.StandardLibraries


object CassieBuild extends Build with StandardLibraries {

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
  ).aggregate(core, customer, service, test)


  lazy val core = Project(
    id = "cassie-core",
    base = file("core"),
    settings = Project.defaultSettings
      ++ sharedSettings
  ).settings(
    name := "cassie-core",
    libraryDependencies ++= Seq(
    ) ++ Libs.commonsCustomer
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
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-service",
    libraryDependencies ++= Seq(
    ) ++ Libs.microservice
      ++ Libs.commonsEvents
      ++ Libs.microservice,
    makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
      var path = dir / "bin" / "cassie-service"
      sbt.Process(Seq("ln", "-sf", path.toString, "cassie-service"), cwd) ! streams.log
    }
  ).dependsOn(test)


  lazy val test = Project(
    id = "cassie-test",
    base = file("test"),
    settings = Project.defaultSettings
      ++ sharedSettings
  )
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cassie-test",
    libraryDependencies ++= Seq(
    ) ++ Libs.microservice
      ++ Libs.commonsEvents
      ++ Libs.microservice
  ).dependsOn()

}