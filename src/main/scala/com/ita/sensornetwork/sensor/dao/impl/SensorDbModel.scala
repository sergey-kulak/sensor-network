package com.ita.sensornetwork.sensor.dao.impl

import java.time.LocalDateTime

import com.ita.sensornetwork.common.BaseDao
import com.ita.sensornetwork.sensor.{MeasurableParameter, Sensor}
import slick.jdbc.GetResult

trait SensorDbModel {
  self: BaseDao =>

  import dbConfig.profile.api._

  val sensors = TableQuery[SensorTable]
  val insertSensorsQuery = insertWithId(sensors) { (sensor, id) => sensor.copy(id = id) }

  final class SensorTable(tag: Tag) extends EntityWithLongTable[Sensor](tag, "sensor") {
    def serialNumber = column[String]("serial_number")

    def registrationDate = column[LocalDateTime]("registration_date")

    def * = (serialNumber, registrationDate, id) <> (rowToSensor, sensorToRow)
  }

  private def rowToSensor(row: (String, LocalDateTime, Long)): Sensor = {
    Sensor(row._1, row._2, Set.empty, row._3)
  }

  private def sensorToRow(sensor: Sensor) = Option((sensor.serialNumber, sensor.registrationDate, sensor.id))



}
