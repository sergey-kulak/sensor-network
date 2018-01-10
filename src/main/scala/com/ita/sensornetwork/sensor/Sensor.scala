package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.common.Entity

case class Sensor(
                   serialNumber: String,
                   registrationDate: LocalDateTime = LocalDateTime.now(),
                   measurableParameters: Set[MeasurableParameter] = Set.empty,
                   id: Long = 0) extends Entity[Long]

object Sensor extends ((String, LocalDateTime, Set[MeasurableParameter], Long) => Sensor) {
  val SerialNumber = "serialNumber"
}

