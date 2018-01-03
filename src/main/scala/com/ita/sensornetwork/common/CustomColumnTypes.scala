package com.ita.sensornetwork.common

import java.sql.Timestamp
import java.time.LocalDateTime

trait CustomColumnTypes {
  self: DbConfigAware =>

  import dbConfig.profile.api._

  implicit def localDateTimeToTimestampMapping = MappedColumnType.base[LocalDateTime, Timestamp](
    ld => localDateTimeToTimestamp(ld),
    dt => dt.toLocalDateTime
  )

  implicit def localDateTimeToTimestamp(ldt: LocalDateTime): Timestamp = Timestamp.valueOf(ldt)

}
