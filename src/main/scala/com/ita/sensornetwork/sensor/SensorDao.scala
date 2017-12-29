package com.ita.sensornetwork.sensor

import scala.concurrent.Future

trait SensorDao {
  def register(registerSensor: RegisterSensor): Future[Sensor]

  def findAll(): Future[Seq[Sensor]]
}
