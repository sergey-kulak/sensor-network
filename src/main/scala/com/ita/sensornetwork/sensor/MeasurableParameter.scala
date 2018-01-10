package com.ita.sensornetwork.sensor

import enumeratum.{EnumEntry, Enum}

sealed abstract class MeasurableParameter(override val entryName: String) extends EnumEntry

object MeasurableParameter extends Enum[MeasurableParameter]{

  case object Location extends MeasurableParameter("LOC")

  case object Temperature extends MeasurableParameter("TEMP")

  case object Humidity extends MeasurableParameter("HUMD")

  case object NoiseLevel extends MeasurableParameter("NS")

  def values = findValues
}