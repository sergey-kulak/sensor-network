package com.ita.sensornetwork

import com.ita.sensornetwork.common.FlywayMigration
import com.ita.sensornetwork.sensor.{SensorDao, SensorDaoImpl}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App with FlywayMigration {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")
  migrate(dbConfig.config)

  val sensorDao: SensorDao = new SensorDaoImpl(dbConfig)
  val result = Await.result(sensorDao.findAll(), 2 seconds)
  println(s"result size: ${result.size}")
}
