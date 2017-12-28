package com.ita.sensornetwork.common

import slick.jdbc.JdbcProfile

trait DbProvider {
  val profile: JdbcProfile

  import profile.api._

  val db: Database
}