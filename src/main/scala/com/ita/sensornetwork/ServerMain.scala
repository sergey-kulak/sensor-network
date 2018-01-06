package com.ita.sensornetwork

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.ita.sensornetwork.Main.migrate
import com.ita.sensornetwork.common.{DbConfigAware, ExecutionContextAware}
import com.ita.sensornetwork.sensor.dao.impl.SensorDaoImplComponent
import com.ita.sensornetwork.sensor.rest.SensorRestApiComponent
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object ServerMain extends App {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")
  migrate(dbConfig.config)

  implicit val system = ActorSystem("sensor-network")
  implicit val materializer = ActorMaterializer()
  implicit val akkaExecutionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(ComponentRegistry.restApi.routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

  private object ComponentRegistry
    extends DbConfigAware
      with ExecutionContextAware
      with SensorDaoImplComponent
      with SensorRestApiComponent
      with RestApiComponent {
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("sensor-network-db")

    override implicit def executionContext: ExecutionContext = akkaExecutionContext
  }

}
