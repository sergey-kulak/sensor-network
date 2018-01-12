package com.ita.sensornetwork.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import enumeratum._
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


  implicit object SortDirectionFormat extends EnumJsonFormat[SortDirection] {

    def parseFromCode(code: String): Option[SortDirection] = SortDirection.withNameInsensitiveOption(code)
  }

  trait EnumJsonFormat[E <: EnumEntry] extends JsonFormat[E] {

    def write(sd: E): JsValue = JsString(sd.entryName)

    def read(json: JsValue): E = json match {
      case JsString(code) =>
        parseFromCode(code)
          .fold(deserializationError(s"Expected current enum value, but got $code"))(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }

    def parseFromCode(code: String): Option[E]
  }

}
