import Libraries._

name := "AGH_Fuzzy"

version := "0.1"

scalaVersion := "2.12.7"

lazy val root = (project in file("."))
  .aggregate(
    analysis,
    endoImporter
  )

lazy val endoImporter = (project in file("endoImporter"))
  .settings(
    libraryDependencies ++= Seq(
      betterFiles,
      scalaLogging,
      typesafeConfig
    )
  )

lazy val analysis = (project in file("analysis"))
  .settings(
    libraryDependencies ++= Seq(
      akkaStreams,
      alpakkaFiles,
      betterFiles,
      typesafeConfig,
      scalaLogging,
    )++ circe
  )