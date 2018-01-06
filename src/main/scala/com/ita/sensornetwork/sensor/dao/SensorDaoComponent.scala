package com.ita.sensornetwork.sensor.dao

import com.ita.sensornetwork.common.Page
import com.ita.sensornetwork.sensor._

import scala.concurrent.Future

trait SensorDaoComponent {

  def sensorDao: SensorDao

  trait SensorDao {

    def findById(id: Long): Future[Option[Sensor]]

    def register(registerSensor: RegisterSensor): Future[Sensor]

    def findAll(): Future[Seq[Sensor]]

    def saveSensorData(sensorId: Long, sensorData: CreateSensorData): Future[SensorData]

    def findSensorData(filter: SensorDataFilter): Future[Page[FullSensorData]]

    def findSensorMaxStatistics(filter: SensorMaxStatisticsFilter): Future[Seq[(Sensor, Option[SensorData])]]
  }

}


