package com.ita.sensornetwork.sensor

import enumeratum.EnumEntry

sealed abstract class MeasurableParameter(override val entryName: String) extends EnumEntry

// to support relation between MeasurableParameter and its value but avoid MeasurableParameter parametrization
trait ValueTypeHolder {
  type ValueType

  def toValue(text: String): ValueType

  def toText(value: ValueType): String = value.toString
}

case class Measure[T](parameter: MeasurableParameter with ValueTypeHolder {type ValueType = T}, value: T) {
  def valueToText: String = parameter.toText(value)
}

object Measure {

  def of(parameter: MeasurableParameter, value: String): Measure[_] = {
    val nonTextValue = parameter.asInstanceOf[ValueTypeHolder].toValue(value)
    Measure.of(parameter, nonTextValue)
  }

  def of(parameter: MeasurableParameter, value: Any): Measure[_] = {
    (parameter, value) match {
      case (p: GeoLocationTypeHolder, value: GeoLocation) => Measure(p, value)
      case (p: StringTypeHolder, value: String) => Measure(p, value)
      case (p: DoubleTypeHolder, value: Double) => Measure(p, value)
    }
  }
}

// for numeric
trait DoubleTypeHolder extends ValueTypeHolder {
  override type ValueType = Double

  override def toValue(text: String): Double = text.toDouble
}

abstract class DoubleMeasurableParameter(entryName: String) extends MeasurableParameter(entryName) with DoubleTypeHolder


// for string
trait StringTypeHolder extends ValueTypeHolder {
  override type ValueType = String

  override def toValue(text: String): String = text
}

abstract class StringMeasurableParameter(entryName: String) extends MeasurableParameter(entryName) with StringTypeHolder

// for geo location
case class GeoLocation(lat: Double, long: Double)

trait GeoLocationTypeHolder extends ValueTypeHolder {
  override type ValueType = GeoLocation

  override def toValue(text: String): GeoLocation = {
    val Array(lat, long) = text.split(',').map(_.toDouble)
    GeoLocation(lat, long)
  }

  override def toText(value: GeoLocation): String = s"${value.lat},${value.long}"
}

abstract class GeoLocationMeasurableParameter(entryName: String) extends MeasurableParameter(entryName) with GeoLocationTypeHolder


object MeasurableParameter extends enumeratum.Enum[MeasurableParameter] {

  case object Location extends GeoLocationMeasurableParameter("LOC")

  case object Temperature extends DoubleMeasurableParameter("TEMP")

  case object Humidity extends DoubleMeasurableParameter("HUMD")

  case object NoiseLevel extends DoubleMeasurableParameter("NS")

  case object CellId extends StringMeasurableParameter("CID")

  def values = findValues
}