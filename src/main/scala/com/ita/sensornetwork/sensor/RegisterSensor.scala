package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

case class RegisterSensor(
                           serialNumber: String,
                           registrationDate: LocalDateTime = LocalDateTime.now())