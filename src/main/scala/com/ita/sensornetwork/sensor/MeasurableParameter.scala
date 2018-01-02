package com.ita.sensornetwork.sensor

class MeasurableParameter(val code: String)

object MeasurableParameter {

  object Location extends MeasurableParameter("LOC")

  object Temperature extends MeasurableParameter("TEMP")

  object Humidity extends MeasurableParameter("HUMD")

  object NoiseLevel extends MeasurableParameter("NS")

  def values = Array(Location, Temperature, Humidity, NoiseLevel)

  def foundByCode(code: String): Option[MeasurableParameter] = {
    MeasurableParameter.values.find(_.code == code)
  }
}