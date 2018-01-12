package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.common.{Entity, PageRequest}
import com.ita.sensornetwork.sensor.MeasurableParameter.Humidity

case class SensorData(sensorId: Long,
                      measure: Measure[_],
                      time: LocalDateTime = LocalDateTime.now(),
                      id: Long = 0L) extends Entity[Long]

object SensorDataField {
  val Time = "time"
}

case class CreateSensorData(measure: Measure[_],
                            time: LocalDateTime = LocalDateTime.now()) {
  measure match {
    case Measure(Humidity, value: Double) => require(value >= 0, "Humidity can't be negative")
    case _ =>
  }
}

case class RawSensorData(measureParameter: MeasurableParameter,
                         value: String,
                         time: LocalDateTime = LocalDateTime.now())

case class FullSensorData(sensor: Sensor,
                          measure: Measure[_],
                          time: LocalDateTime,
                          id: Long)

case class SensorDataFilter(pageRequest: PageRequest,
                            sensorId: Option[Long] = None,
                            sensorSerialNumber: Option[String] = None)
