package com.ita.sensornetwork

import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val db = Database.forConfig("sensor-network-db")
}
