package com.ita.sensornetwork.sensor


case class Sensor(
                   serialNumber: String,
                   id: Long = 0L
                   //,registrationDate: LocalDateTime
                   //,sensorParameters: Seq[SensorParameter]
                 )

