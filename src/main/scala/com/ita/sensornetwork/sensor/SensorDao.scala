package com.ita.sensornetwork.sensor

import scala.concurrent.Future

trait SensorDao {
  def findAll(): Future[Seq[Sensor]]
}
