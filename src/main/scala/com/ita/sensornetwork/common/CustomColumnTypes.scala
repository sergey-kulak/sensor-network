package com.ita.sensornetwork.common

import java.sql.Timestamp
import java.time.LocalDateTime

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait CustomColumnTypes {
  this: {
    val dbConfig: DatabaseConfig[JdbcProfile]
  } =>

  import dbConfig.profile.api._

  implicit val localDateTimeToTimestamp = MappedColumnType.base[LocalDateTime, Timestamp](
    ld => Timestamp.valueOf(ld),
    dt => dt.toLocalDateTime
  )

}
