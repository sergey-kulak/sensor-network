package com.ita.sensornetwork

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ita.sensornetwork.health.rest.HealthRestApi
import com.ita.sensornetwork.sensor.rest.SensorRestApiComponent

trait RestApiComponent {
  this: SensorRestApiComponent =>

  def restApi: RestApi = new RestApi {}

  trait RestApi {

    def routes = {
      val routeChain: Seq[Route] = Seq(
        HealthRestApi.routes,
        sensorRestApi.routes
      )
      routeChain.reduce(_ ~ _)
    }
  }

}


