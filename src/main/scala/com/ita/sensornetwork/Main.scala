package com.ita.sensornetwork

import com.ita.sensornetwork.sensor.{SensorDao, SensorDaoImpl}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")
  //  val dbProvider = new DbProvider {
  //    override val profile = dbConfig.profile
  //    override val db = dbConfig.db
  //  }
  val sensorDao: SensorDao = new SensorDaoImpl(dbConfig)

  val result = Await.result(sensorDao.findAll(), 2 seconds)
  println(s"result: ${result}")
}
