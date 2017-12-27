package com.ita.sensornetwork.sensor

import java.time.LocalDateTime


case class Sensor(id: Long, serialNumber: String, registrationDate: LocalDateTime, sensorTypes: Seq[SensorType])

