package com.ita.sensornetwork

import com.ita.sensornetwork.sensor.{SensorDao, SensorDaoImpl}
import com.typesafe.config.Config
import org.flywaydb.core.Flyway
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")
  migrate(dbConfig.config)

  val sensorDao: SensorDao = new SensorDaoImpl(dbConfig)
  val result = Await.result(sensorDao.findAll(), 2 seconds)
  println(s"result: ${result}")

  private def migrate(config: Config): Unit = {
    val flyway: Flyway = new Flyway
    val url = config.getString("db.url")
    val userName = config.getString("db.user")
    flyway.setDataSource(url, userName, null)
    flyway.migrate()
  }
}
