package com.ita.sensornetwork.sensor

sealed abstract class MeasurableParameter(val code: String)

object MeasurableParameter {

  case object Location extends MeasurableParameter("LOC")

  case object Temperature extends MeasurableParameter("TEMP")

  case object Humidity extends MeasurableParameter("HUMD")

  case object NoiseLevel extends MeasurableParameter("NS")

  def values = Array(Location, Temperature, Humidity, NoiseLevel)

  def foundByCode(code: String): Option[MeasurableParameter] = values.find(_.code == code)
}