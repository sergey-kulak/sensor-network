package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.common._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ColumnOrdered

import scala.concurrent.{ExecutionContext, Future}

class SensorDaoImpl(val dbConfig: DatabaseConfig[JdbcProfile])(implicit executor: ExecutionContext) extends SensorDao with CustomColumnTypes {

  import dbConfig.profile.api._

  val db = dbConfig.db
  val sensors = TableQuery[SensorTable]
  val insertSensorsQuery = sensors returning sensors.map(_.id) into { (sensor, id) => sensor.copy(id = id) }

  val sensorMeasurableParameters = TableQuery[SensorMeasurableParameterTable]

  val sensorDataItems = TableQuery[SensorDataTable]
  val insertSensorDataQuery = sensorDataItems returning sensorDataItems.map(_.id) into {
    (sensorData, id) => sensorData.copy(id = id)
  }

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
    sensors.result
      .flatMap { sensors =>
        val ids = sensors.map(_.id)
        sensorMeasurableParameters.filter(_.sensorId inSet ids).result
          .map { sensorParams =>
            val sensorParamMap = sensorParams.groupBy(_._1).mapValues(_.map(_._2).toSet)
            sensors.map(s => s.copy(measurableParameters = sensorParamMap.getOrElse(s.id, Set.empty)))
          }
      }.transactionally
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
    val query = sensorDataItems
      .join(sensors).on(_.sensorId === _.id)
      .filter { case (sd, s) => buildFilter(sd, s, filter) }
    val pageRequest = filter.pageRequest
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
    Seq(filter.sensorId.map(sd.sensorId === _),
      filter.sensorSerialNumber.map(s.serialNumber === _)
    ).flatten.fold(true.bind)(_ && _)
  }

  private def buildSort(sd: SensorDataTable, s: SensorTable, sort: Sort) = {
    val sortField: ColumnOrdered[_] = sort.field match {
      case PageRequest.IdField => sd.id
      case SensorField.SerialNumber => s.serialNumber
      case SensorDataField.Time => sd.time
    }
    sort.sortDirection match {
      case SortDirection.Asc => sortField.asc
      case SortDirection.Desc => sortField.desc
    }
  }

  final class SensorTable(tag: Tag) extends Table[Sensor](tag, "sensor") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def serialNumber = column[String]("serial_number")

    def registrationDate = column[LocalDateTime]("registration_date")

    def * = (serialNumber, registrationDate, id) <> (rowToSensor, sensorToRow)
  }

  private def rowToSensor(row: (String, LocalDateTime, Long)): Sensor = {
    Sensor(row._1, row._2, Set.empty, row._3)
  }

  private def sensorToRow(sensor: Sensor) = Option((sensor.serialNumber, sensor.registrationDate, sensor.id))

  implicit def measurableParameterToString = MappedColumnType.base[MeasurableParameter, String](
    mp => mp.code,
    cd => MeasurableParameter.values.find(_.code == cd).get
  )

  final class SensorMeasurableParameterTable(tag: Tag) extends Table[(Long, MeasurableParameter)](tag, "sensor_measurable_parameter") {
    def sensorId = column[Long]("sensor_id")

    def measurableParameter = column[MeasurableParameter]("measurable_parameter")

    def * = (sensorId, measurableParameter)
  }

  final class SensorDataTable(tag: Tag) extends Table[SensorData](tag, "sensor_data") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def sensorId = column[Long]("sensor_id")

    def measurableParameter = column[MeasurableParameter]("measurable_parameter")

    def value = column[Double]("value")

    def time = column[LocalDateTime]("time")

    def * = (sensorId, measurableParameter, value, time, id).mapTo[SensorData]

    def sensor = foreignKey("sensor_lf", sensorId, sensors)(_.id)
  }

}

