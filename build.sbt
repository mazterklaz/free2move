ThisBuild / version := "0.1.0-SNAPSHOT"

name := "free2move"

ThisBuild / scalaVersion := "2.12.15"


val ScalaTestVersion = "3.2.11"
val TypesafeVersion = "1.4.2"
val LogbackVersion = "1.2.10"
val ScalaLoggingVersion = "3.9.4"
val HadoopVersion = "3.3.1"
val SparkVersion = "3.2.1"
val JacksonVersion = "2.12.0"
val PureConfigVersion = "0.17.1"

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
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % JacksonVersion
)
lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % SparkVersion,
  "org.apache.spark" %% "spark-sql" % SparkVersion
)


libraryDependencies ++= sparkDependencies ++ commonDependencies
dependencyOverrides ++= jacksonDependencies

assembly / assemblyJarName := name.value + ".jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _ @_*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case _ => MergeStrategy.last
}
assembly / test := {}
assembly / mainClass := Some("main.Free2MoveApp")
assembly / assemblyShadeRules := Seq(ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll)


resolvers ++= Seq(
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("public"),
  Resolver.bintrayRepo("ovotech", "maven"),
)

Compile / run := Defaults.runTask(Compile / fullClasspath, mainClass in (Compile, run), runner in (Compile, run))