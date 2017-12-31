package com.ita.sensornetwork.sensor

import java.time.LocalDateTime


case class Sensor(
                   serialNumber: String,
                   registrationDate: LocalDateTime,
                   measurableParameters: Set[MeasurableParameter] = Set.empty,
                   id: Long = 0L)

object SensorField {
  val SerialNumber = "serialNumber"
}

