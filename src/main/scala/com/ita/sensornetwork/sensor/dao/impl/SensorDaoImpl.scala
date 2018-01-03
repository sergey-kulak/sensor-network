package com.ita.sensornetwork.sensor.dao.impl

import com.ita.sensornetwork.common._
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDao
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ColumnOrdered

import scala.concurrent.{ExecutionContext, Future}

class SensorDaoImpl(val dbConfig: DatabaseConfig[JdbcProfile])(implicit executor: ExecutionContext)
  extends BaseDao with SensorDao with SensorDbModel with SensorDataDbModel {

  import dbConfig.profile.api._

  val db = dbConfig.db

  def register(registerSensor: RegisterSensor): Future[Sensor] = {
    db.run(registerAction(registerSensor))
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
    for {
      sensors <- sensors.result
      ids <- DBIO.successful(sensors.map(_.id))
      sensorParams <- sensorMeasurableParameters.filter(_.sensorId inSet ids).result
    } yield {
      val sensorParamMap = sensorParams.groupBy(_._1).mapValues(_.map(_._2).toSet)
      sensors.map(s => s.copy(measurableParameters = sensorParamMap.getOrElse(s.id, Set.empty)))
    }
  }

  override def saveSensorData(sensorData: SensorData): Future[SensorData] = {
    db.run(saveSensorDataAction(sensorData))
  }

  def saveSensorDataAction(sensorData: SensorData): DBIO[SensorData] = {
    insertSensorDataQuery += sensorData
  }

  override def findSensorData(filter: SensorDataFilter): Future[Page[FullSensorData]] = {
    db.run(findSensorDataAction(filter))
  }

  def findSensorDataAction(filter: SensorDataFilter): DBIO[Page[FullSensorData]] = {
    val pageRequest = filter.pageRequest

    val query = sensorDataItems
      .join(sensors).on(_.sensorId === _.id)
      .filter { case (sd, s) => buildFilter(sd, s, filter) }

    for {
      count <- query.length.result
      content <- query.sortBy { case (sd, s) => buildSort(sd, s, pageRequest.sort) }
        .drop(pageRequest.startIndex).take(pageRequest.length).result if count > 0
    } yield Page(toFullSensorData(content), pageRequest.pageNumber, pageRequest.pageCount(count), count)
  }

  private def toFullSensorData(items: Seq[(SensorData, Sensor)]): Seq[FullSensorData] = {
    items.map { case (sd, s) => FullSensorData.of(sd, s) }
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
      .map { case ((sId, mp), items) => (sId, mp) -> items.map(_.value).max }

    sensorDataItems.join(maxData)
      .on { case (sd, (mdId, mdValue)) => sd.sensorId === mdId._1 && sd.measurableParameter === mdId._2 &&
        sd.value === mdValue
      }
      .joinRight(sensors).on { case ((sd, _), s) => sd.sensorId === s.id }
      .map { case (md, s) => (s, md.map(_._1)) }.result
  }

  private def buildFilter(sd: SensorDataTable, filter: SensorMaxStatisticsFilter): Rep[Boolean] = {
    joinAnd(
      filter.from.map(sd.time >= _),
      filter.to.map(sd.time <= _))
  }


}

