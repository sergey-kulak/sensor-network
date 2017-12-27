package com.ita.sensornetwork.sensor

sealed trait SensorType

object SensorType {

  case object Location extends SensorType

  case object Temperature extends SensorType

  case object Humidity extends SensorType

}