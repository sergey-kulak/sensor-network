package com.ita.sensornetwork.sensor.dao.impl

import com.ita.sensornetwork.common.BaseDao
import com.ita.sensornetwork.sensor.MeasurableParameter
import slick.jdbc.GetResult

trait SensorMeasurableParameterDbModel {
  self: BaseDao =>

  import dbConfig.profile.api._

  val sensorMeasurableParameters = TableQuery[SensorMeasurableParameterTable]

  implicit def measurableParameterToString = MappedColumnType.base[MeasurableParameter, String](
    mp => mp.code,
    cd => MeasurableParameter.foundByCode(cd).get
  )

  implicit val GetMeasurableParameter = GetResult(r => MeasurableParameter.foundByCode(r.nextString).get)

  final class SensorMeasurableParameterTable(tag: Tag) extends Table[(Long, MeasurableParameter)](tag, "sensor_measurable_parameter") {
    def sensorId = column[Long]("sensor_id")

    def measurableParameter = column[MeasurableParameter]("measurable_parameter")

    def * = (sensorId, measurableParameter)
  }

}
