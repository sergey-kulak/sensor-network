package com.ita.sensornetwork

import java.time.LocalDateTime

import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.impl.SensorDaoImpl

import scala.util.Random

class TestEntityKit extends SlickTestKit("sensor-network-db") {

  import dbConfig.profile.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  val sensorDao = new SensorDaoImpl(dbConfig)

  def registerSensor(): DBIO[Sensor] = {
    registerSensor(Set(MeasurableParameter.Temperature, MeasurableParameter.NoiseLevel))
  }

  def registerSensor(measurableParameters: Set[MeasurableParameter]): DBIO[Sensor] = {
    val registerDto = RegisterSensor(Random.nextString(10), LocalDateTime.now(), measurableParameters)
    sensorDao.registerAction(registerDto)
  }

  def addSensorData(sensor: Sensor, value: Double = Random.nextDouble(),
                    time: LocalDateTime = LocalDateTime.now()): DBIO[SensorData] = {
    val sensorData = SensorData(sensor.id, sensor.measurableParameters.head, value, time)
    sensorDao.saveSensorDataAction(sensorData)
  }
}
