package com.ita.sensornetwork

import java.time.LocalDateTime

import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.impl.SensorDaoImplComponent

import scala.util.Random

class TestEntityKit extends SlickTestKit("sensor-network-db") with SensorDaoImplComponent {

  import dbConfig.profile.api._

  def registerSensor(): DBIO[Sensor] = {
    registerSensor(Set(MeasurableParameter.Temperature, MeasurableParameter.NoiseLevel))
  }

  def registerSensor(measurableParameters: Set[MeasurableParameter]): DBIO[Sensor] = {
    val registerDto = RegisterSensor(Random.nextString(10), LocalDateTime.now(), measurableParameters)
    sensorDao.registerAction(registerDto)
  }

  def addSensorData(sensor: Sensor, value: Double = Random.nextDouble(),
                    time: LocalDateTime = LocalDateTime.now()): DBIO[SensorData] = {
    val sensorData = CreateSensorData(sensor.measurableParameters.head, value, time)
    sensorDao.saveSensorDataAction(sensor.id, sensorData)
  }
}
