package com.ita.sensornetwork.sensor.dao.impl

import java.time.LocalDateTime

import com.ita.sensornetwork.common.{BaseDao, Entity}
import com.ita.sensornetwork.sensor._

trait SensorDataDbModel extends SensorMeasurableParameterDbModel {
  self: BaseDao with SensorDbModel =>

  import dbConfig.profile.api._

  case class SensorDataRow(sensorId: Long,
                           measurableParameter: MeasurableParameter,
                           numValue: Option[Double] = None,
                           strValue: Option[String] = None,
                           geoLat: Option[Double] = None,
                           geoLong: Option[Double] = None,
                           time: LocalDateTime,
                           id: Long = 0L) extends Entity[Long]

  val sensorDataItems = TableQuery[SensorDataTable]
  val insertSensorDataQuery = insertWithId(sensorDataItems) { (sensorData, id) => sensorData.copy(id = id) }

  final class SensorDataTable(tag: Tag) extends EntityWithLongTable[SensorDataRow](tag, "sensor_data") {
    def sensorId = column[Long]("sensor_id")

    def measurableParameter = column[MeasurableParameter]("measurable_parameter")

    def numValue = column[Option[Double]]("num_value")

    def strValue = column[Option[String]]("str_value")

    def geoLat = column[Option[Double]]("geo_lat")

    def geoLong = column[Option[Double]]("geo_long")

    def time = column[LocalDateTime]("time")

    def * = (sensorId, measurableParameter, numValue, strValue, geoLat, geoLong, time, id).mapTo[SensorDataRow]

    def sensor = foreignKey("sensor_lf", sensorId, sensors)(_.id)
  }

  protected def toMeasure(sdr: SensorDataRow): Measure[_] = {
    sdr.measurableParameter match {
      case p: GeoLocationTypeHolder => Measure(p, GeoLocation(sdr.geoLat.get, sdr.geoLong.get))
      case p: StringTypeHolder => Measure(p, sdr.strValue.get)
      case p: DoubleTypeHolder => Measure(p, sdr.numValue.get)
    }
  }

  protected def toSensorData(sdr: SensorDataRow): SensorData = {
    SensorData(sdr.sensorId, toMeasure(sdr), sdr.time, sdr.id)
  }

}
