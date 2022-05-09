ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"


val ScalaTestVersion = "3.2.11"
val TypesafeVersion = "1.4.2"
val LogbackVersion = "1.2.10"
val ScalaLoggingVersion = "3.9.4"
val HadoopVersion = "3.3.2"
val SparkVersion = "3.2.1"
val JacksonVersion = "2.12.0"
val PureConfigVersion = "0.17.1"

lazy val assemblySettings = Seq(
  assembly / assemblyJarName := name.value + ".jar",
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }
)

lazy val commonDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
  "com.typesafe" % "config" % TypesafeVersion,
  "ch.qos.logback" % "logback-classic" % LogbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
  "org.apache.hadoop" % "hadoop-client" % HadoopVersion,
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
)
lazy val jacksonDependencies = Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.13" % JacksonVersion
)
lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % SparkVersion,
  "org.apache.spark" %% "spark-sql" % SparkVersion
)


lazy val root = (project in file("."))
  .settings(
    name := "free2move",
    libraryDependencies ++= sparkDependencies ++ commonDependencies,
    dependencyOverrides ++= jacksonDependencies
  )
