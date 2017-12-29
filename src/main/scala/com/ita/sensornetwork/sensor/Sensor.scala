package com.ita.sensornetwork.sensor

import java.time.LocalDateTime


case class Sensor(
                   serialNumber: String,
                   registrationDate: LocalDateTime,
                   id: Option[Long] = None
                   //,sensorParameters: Seq[SensorParameter]
                 )

