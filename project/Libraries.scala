import sbt._

object Libraries {

  object Versions {
    lazy val akkaStreams = "2.5.18"
    lazy val alpakkaFiles = "1.0-M1"
    lazy val typesafeConfig = "1.3.2"
    lazy val logback = "1.2.3"
    lazy val scalaLogging = "3.9.0"
    lazy val betterFiles = "3.6.0"
    lazy val circe = "0.10.0"
  }

  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % Versions.akkaStreams
  lazy val alpakkaFiles = "com.lightbend.akka" %% "akka-stream-alpakka-file" % Versions.alpakkaFiles
  lazy val typesafeConfig = "com.typesafe" % "config" % Versions.typesafeConfig
  lazy val logging = "ch.qos.logback" % "logback-classic" % Versions.logback
  lazy val scalaLogging =  "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
  lazy val betterFiles = "com.github.pathikrit" %% "better-files" % Versions.betterFiles
  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.circe)
}

