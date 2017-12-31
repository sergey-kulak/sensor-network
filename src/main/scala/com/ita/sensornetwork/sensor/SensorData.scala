package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.common.PageRequest

case class SensorData(sensorId: Long,
                      measurableParameter: MeasurableParameter,
                      value: Double,
                      time: LocalDateTime = LocalDateTime.now(),
                      id: Long = 0L)

object SensorDataField {
  val Time = "time"
}

case class FullSensorData(sensor: Sensor,
                          measurableParameter: MeasurableParameter,
                          value: Double,
                          time: LocalDateTime,
                          id: Long)

object FullSensorData {
  def of(sd: SensorData, sensor: Sensor) = FullSensorData(sensor = sensor,
    measurableParameter = sd.measurableParameter, value = sd.value,
    time = sd.time, id = sd.id
  )
}

case class SensorDataFilter(pageRequest: PageRequest,
                            sensorId: Option[Long] = None,
                            sensorSerialNumber: Option[String] = None)
