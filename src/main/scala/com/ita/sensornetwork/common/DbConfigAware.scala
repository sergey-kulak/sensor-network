package com.ita.sensornetwork.common

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DbConfigAware {
  val dbConfig: DatabaseConfig[JdbcProfile]
}
