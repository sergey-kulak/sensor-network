package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

case class SensorMaxStatisticsFilter(from: Option[LocalDateTime] = None,
                                     to: Option[LocalDateTime]= None,
                                     measurableParameter: Seq[MeasurableParameter]= Seq.empty)

