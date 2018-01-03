package com.ita.sensornetwork.sensor.dao.impl

import java.time.LocalDateTime

import com.ita.sensornetwork.common.BaseDao
import com.ita.sensornetwork.sensor.{MeasurableParameter, SensorData}

trait SensorDataDbModel extends SensorMeasurableParameterDbModel {
  self: BaseDao with SensorDbModel =>

  import dbConfig.profile.api._

  val sensorDataItems = TableQuery[SensorDataTable]
  val insertSensorDataQuery = insertWithId(sensorDataItems) { (sensorData, id) => sensorData.copy(id = id) }

  final class SensorDataTable(tag: Tag) extends EntityWithLongTable[SensorData](tag, "sensor_data") {
    def sensorId = column[Long]("sensor_id")

    def measurableParameter = column[MeasurableParameter]("measurable_parameter")

    def value = column[Double]("value")

    def time = column[LocalDateTime]("time")

    def * = (sensorId, measurableParameter, value, time, id).mapTo[SensorData]

    def sensor = foreignKey("sensor_lf", sensorId, sensors)(_.id)
  }

}
