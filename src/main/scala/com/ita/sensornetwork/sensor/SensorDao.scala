package com.ita.sensornetwork.sensor

import com.ita.sensornetwork.common.{Page, PageRequest}

import scala.concurrent.Future

trait SensorDao {
  def register(registerSensor: RegisterSensor): Future[Sensor]

  def findAll(): Future[Seq[Sensor]]

  def saveSensorData(sensorData: SensorData): Future[SensorData]

  def findSensorData(filter: SensorDataFilter): Future[Page[FullSensorData]]
}
