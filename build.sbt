name := "sensor-network"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  val logbackVersion = "1.1.3"
  val slickVersion = "3.2.1"
  val akkaVersion = "2.5.8"
  val akkaHttpVersion = "10.0.11"
  val h2Version = "1.4.185"

  val scalatestVersion = "3.0.4"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.h2database" % "h2" % h2Version
  )
}