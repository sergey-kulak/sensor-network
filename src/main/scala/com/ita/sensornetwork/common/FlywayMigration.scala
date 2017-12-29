package com.ita.sensornetwork.common

import com.typesafe.config.Config
import org.flywaydb.core.Flyway

trait FlywayMigration {
  private val DbUrl = "db.url"
  private val DbUser = "db.url"
  private val DbPassword = "db.password"

  def migrate(config: Config): Unit = {
    val flyway: Flyway = new Flyway
    val url = config.getString(DbUrl)
    val user = config.getString(DbUser)
    val password = config.getString(DbPassword)
    flyway.setDataSource(url, user, password)

    flyway.migrate()
  }

}
