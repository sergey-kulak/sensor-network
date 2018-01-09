name := "sensor-network"

version := "0.1"

scalaVersion := "2.12.4"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
  val logbackVersion = "1.1.3"
  val slickVersion = "3.2.1"
  val akkaVersion = "2.5.8"
  val akkaHttpVersion = "10.0.11"
  val h2Version = "1.4.185"
  val flywayVersion = "5.0.3"
  val commonsLangVersion = "3.7"

  val scalatestVersion = "3.0.4"
  val scalamockVersion = "4.0.0"
  Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion,

    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.h2database" % "h2" % h2Version,
    "org.flywaydb" % "flyway-core" % flywayVersion,

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.apache.commons" % "commons-lang3" % commonsLangVersion,

    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.scalamock" %% "scalamock" % scalamockVersion % "test"

  )
}

mainClass in Compile := Some("com.ita.sensornetwork.ServerMain")