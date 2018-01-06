package com.ita.sensornetwork.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.ita.sensornetwork.sensor.MeasurableParameter
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, deserializationError}

import scala.util.Try


trait JsonProtocol extends DefaultJsonProtocol {

  implicit object LocalDateTimeFormat extends JsonFormat[LocalDateTime] {
    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    def write(ldt: LocalDateTime) = JsString(ldt.toString)

    def read(json: JsValue) = json match {
      case JsString(rawDate) =>
        Try {
          LocalDateTime.parse(rawDate, dtf)
        }.toOption
          .fold(deserializationError(s"Expected ISO Date format, got $rawDate"))(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }
  }

  implicit object MeasurableParameterFormat extends JsonFormat[MeasurableParameter] {

    def write(mp: MeasurableParameter) = JsString(mp.code)

    def read(json: JsValue) = json match {
      case JsString(code) =>
        MeasurableParameter.foundByCode(code)
          .fold(deserializationError(s"Expected ISO Date format, got $code"))(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }
  }

}
