package com.ita.sensornetwork.sensor.dao.impl

import com.ita.sensornetwork.common._
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDaoComponent
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ColumnOrdered

import scala.concurrent.Future

trait SensorDaoImplComponent extends SensorDaoComponent {
  this: DbConfigAware with ExecutionContextAware =>

  def sensorDao = new SensorDaoImpl(dbConfig)

  class SensorDaoImpl(val dbConfig: DatabaseConfig[JdbcProfile])
    extends BaseDao with SensorDao with SensorDbModel with SensorDataDbModel {

    import dbConfig.profile.api._

    val db = dbConfig.db

    def findById(id: Long): Future[Option[Sensor]] = {
      db.run(findByIdAction(id))
    }

    def findByIdAction(id: Long): DBIO[Option[Sensor]] = {
      sensors.filter(_.id === id).result.flatMap(appendMeasurableParameters)
        .map(items => items.headOption)
    }

    def register(registerSensor: RegisterSensor): Future[Sensor] = {
      db.run(registerAction(registerSensor).withPinnedSession)
    }

    def registerAction(registerSensor: RegisterSensor): DBIO[Sensor] = {
      val addSensorAction: DBIO[Sensor] =
        insertSensorsQuery += Sensor(registerSensor.serialNumber, registerSensor.registrationDate)

      addSensorAction.flatMap { sensor =>
        val mRows: Set[(Long, MeasurableParameter)] = registerSensor.measurableParameters.map(mp => (sensor.id, mp))

        (sensorMeasurableParameters ++= mRows) >>
          DBIO.successful(sensor.copy(measurableParameters = registerSensor.measurableParameters))
      }.transactionally
    }

    def findAll(): Future[Seq[Sensor]] = {
      db.run(findAllAction())
    }

    def findAllAction(): DBIO[Seq[Sensor]] = {
      sensors.result.flatMap(appendMeasurableParameters)
    }

    private def appendMeasurableParameters(sensors: Sensor*): DBIO[Seq[Sensor]] = {
      for {
        ids <- DBIO.successful(sensors.map(_.id))
        sensorParams <- sensorMeasurableParameters.filter(_.sensorId inSet ids).result
      } yield {
        val sensorParamMap = sensorParams.groupBy(_._1).mapValues(_.map(_._2).toSet)
        sensors.map(s => s.copy(measurableParameters = sensorParamMap.getOrElse(s.id, Set.empty)))
      }
    }

    def saveSensorData(sensorId: Long, sensorData: CreateSensorData): Future[SensorData] = {
      db.run(saveSensorDataAction(sensorId, sensorData))
    }

    def saveSensorDataAction(sensorId: Long, sensorData: CreateSensorData): DBIO[SensorData] = {
      var row = SensorDataRow(sensorId = sensorId, measurableParameter = sensorData.measure.parameter, time = sensorData.time)
      row = sensorData.measure match {
        case Measure(_: GeoLocationMeasurableParameter, value: GeoLocation) => row.copy(geoLat = Some(value.lat), geoLong = Some(value.long))
        case Measure(_: StringMeasurableParameter, value: String) => row.copy(strValue = Some(value))
        case Measure(_: DoubleMeasurableParameter, value: Double) => row.copy(numValue = Some(value))
      }
      (insertSensorDataQuery += row).map(toSensorData)
    }

    def findSensorData(filter: SensorDataFilter): Future[Page[FullSensorData]] = {
      db.run(findSensorDataAction(filter))
    }

    def findSensorDataAction(filter: SensorDataFilter): DBIO[Page[FullSensorData]] = {
      val pageRequest = filter.pageRequest

      val query = sensorDataItems
        .join(sensors).on(_.sensorId === _.id)
        .filter { case (sd, s) => buildFilter(sd, s, filter) }

      val queryWithPaging = query.sortBy { case (sd, s) => buildSort(sd, s, pageRequest.sort) }
        .drop(pageRequest.startIndex).take(pageRequest.length)

      query.length.result.flatMap { count =>
        val contentQuery = if (count > 0) queryWithPaging.result else DBIO.successful(Seq.empty)
        contentQuery.map { content =>
          Page(toFullSensorData(content), pageRequest.pageNumber, pageRequest.pageCount(count), count)
        }
      }
    }

    private def toFullSensorData(items: Seq[(SensorDataRow, Sensor)]): Seq[FullSensorData] = {
      items.map { case (sd, s) => FullSensorData(s, toMeasure(sd), sd.time, sd.id) }
    }

    private def buildFilter(sd: SensorDataTable, s: SensorTable, filter: SensorDataFilter): Rep[Boolean] = {
      joinAnd(
        filter.sensorId.map(sd.sensorId === _),
        filter.sensorSerialNumber.map(s.serialNumber === _))
    }

    private def buildSort(sd: SensorDataTable, s: SensorTable, sort: Sort): ColumnOrdered[_] = {
      buildSort(sort, {
        case PageRequest.IdField => sd.id
        case SensorField.SerialNumber => s.serialNumber
        case SensorDataField.Time => sd.time
      })
    }

    override def findSensorMaxStatistics(filter: SensorMaxStatisticsFilter): Future[Seq[(Sensor, Option[SensorData])]] = {
      db.run(findSensorMaxStatisticsAction(filter))
    }

    def findSensorMaxStatisticsAction(filter: SensorMaxStatisticsFilter): DBIO[Seq[(Sensor, Option[SensorData])]] = {
      val maxData = sensorDataItems
        .filter(buildFilter(_, filter))
        .groupBy(sd => (sd.sensorId, sd.measurableParameter))
        .map { case ((sId, mp), items) => (sId, mp) -> items.map(_.numValue).max }

      sensorDataItems.join(maxData)
        .on { case (sd, (mdId, mdValue)) => sd.sensorId === mdId._1 && sd.measurableParameter === mdId._2 &&
          sd.numValue === mdValue
        }
        .joinRight(sensors).on { case ((sd, _), s) => sd.sensorId === s.id }
        .map { case (md, s) => (s, md.map(_._1)) }.result.map { st =>
        st.map { case (s, sdrOpt) => (s, sdrOpt.map(toSensorData)) }
      }
    }

    private def buildFilter(sd: SensorDataTable, filter: SensorMaxStatisticsFilter): Rep[Boolean] = {
      joinAnd(
        Some(sd.measurableParameter.isInstanceOf[DoubleMeasurableParameter]),
        filter.from.map(sd.time >= _),
        filter.to.map(sd.time <= _))
    }


  }

}