package com.ita.sensornetwork

import com.ita.sensornetwork.common.{DbConfigAware, ExecutionContextAware, FlywayMigration}
import com.ita.sensornetwork.sensor.dao.impl.SensorDaoImplComponent
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object Main extends App with FlywayMigration {
  migrate(ComponentRegistry.dbConfig.config)

  val result = Await.result(ComponentRegistry.sensorDao.findAll(), 2 seconds)
  println(s"result size: ${result.size}")

  private object ComponentRegistry
    extends DbConfigAware
      with ExecutionContextAware
      with SensorDaoImplComponent {
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")

    override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global
  }

}
