package com.ita.sensornetwork.sensor.rest

import com.ita.sensornetwork.common.{JsonProtocol, Page, PageRequest, Sort}
import com.ita.sensornetwork.sensor._
import spray.json.{JsObject, JsString, JsValue, RootJsonFormat}

trait SensorRestApiJsonProtocol extends JsonProtocol {

  implicit object MeasurableParameterFormat extends EnumJsonFormat[MeasurableParameter] {
    def parseFromCode(code: String): Option[MeasurableParameter] = MeasurableParameter.withNameInsensitiveOption(code)
  }

  implicit object MeasureRootFormat extends RootJsonFormat[Measure[_]] {
    private val ParamFieldName = "parameter"
    private val ValueFieldName = "value"

    override def write(obj: Measure[_]): JsValue = {
      val fields = new collection.mutable.ListBuffer[(String, JsValue)]
      fields.sizeHint(2 * 4)
      fields += ((ParamFieldName, MeasurableParameterFormat.write(obj.parameter)))
      fields += ((ValueFieldName, JsString(obj.valueToText)))
      JsObject(fields: _*)
    }

    override def read(json: JsValue): Measure[_] = {
      val jsonObj = json.asJsObject
      val parameter = MeasurableParameterFormat.read(jsonObj.fields(ParamFieldName))
      val textValue = jsonObj.fields(ValueFieldName).asInstanceOf[JsString].value
      Measure.of(parameter, textValue)
    }
  }

  implicit val sensorFormat = jsonFormat4(Sensor)
  implicit val registerSensorFormat = jsonFormat3(RegisterSensor)
  implicit val createSensorDataFormat = jsonFormat2(CreateSensorData)
  implicit val sensorDataFormat = jsonFormat4(SensorData)
  implicit val fullSensorDataFormat = jsonFormat4(FullSensorData)
  implicit val pageFormat = jsonFormat4(Page[FullSensorData])
  implicit val sortFormat = jsonFormat2(Sort)
  implicit val pageRequestFormat = jsonFormat3(PageRequest)
}
