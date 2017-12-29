package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.common.CustomColumnTypes
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class SensorDaoImpl(val dbConfig: DatabaseConfig[JdbcProfile]) extends SensorDao
  with CustomColumnTypes {

  import dbConfig.profile.api._

  val db = dbConfig.db
  val sensors = TableQuery[SensorTable]
  val insertQuery = sensors returning sensors.map(_.id) into { (sensor, id) => sensor.copy(id = id) }

  def register(registerSensor: RegisterSensor): Future[Sensor] = {
    db.run(registerAction(registerSensor))
  }

  def registerAction(registerSensor: RegisterSensor): DBIO[Sensor] = {
    insertQuery += Sensor(registerSensor.serialNumber, registerSensor.registrationDate)
  }

  def findAll(): Future[Seq[Sensor]] = {
    db.run(findAllAction())
  }

  def findAllAction(): DBIO[Seq[Sensor]] = {
    sensors.result
  }

  final class SensorTable(tag: Tag) extends Table[Sensor](tag, "sensor") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def serialNumber = column[String]("serial_number")

    def registrationDate = column[LocalDateTime]("registration_date")

    def * = (serialNumber, registrationDate, id).mapTo[Sensor]
  }

}

