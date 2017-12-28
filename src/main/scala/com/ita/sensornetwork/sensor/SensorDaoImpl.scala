package com.ita.sensornetwork.sensor

import com.ita.sensornetwork.common.DbProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class SensorDaoImpl(val dbProvider: DatabaseConfig[JdbcProfile]) extends SensorDao {

  import dbProvider.profile.api._

  val db = dbProvider.db
  val sensors = TableQuery[SensorTable]

  def findAll(): Future[Seq[Sensor]] = {
    db.run(sensors.result)
  }

  final class SensorTable(tag: Tag) extends Table[Sensor](tag, "sensor") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def serialNumber = column[String]("serial_number")

    //  def registrationDate = column[LocalDateTime]("registration_date")

    def * = (serialNumber, id).mapTo[Sensor]
  }

}