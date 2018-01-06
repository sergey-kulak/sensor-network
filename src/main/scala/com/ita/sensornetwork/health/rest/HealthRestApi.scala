package com.ita.sensornetwork.health.rest

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}

object HealthRestApi {
  def routes = path("health") {
    get {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "I'm up"))
    }
  }
}
