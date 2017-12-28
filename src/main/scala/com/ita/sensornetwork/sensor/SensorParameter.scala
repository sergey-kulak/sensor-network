package com.ita.sensornetwork.sensor

sealed trait SensorParameter

object SensorParameter {

  case object Location extends SensorParameter

  case object Temperature extends SensorParameter

  case object Humidity extends SensorParameter

  case object NoiseLevel extends SensorParameter

}