package com.ita.sensornetwork

import com.ita.sensornetwork.common.FlywayMigration
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{BeforeAndAfterAll, Suite}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


class SlickTestKit(val path: String) extends Suite with BeforeAndAfterAll with FlywayMigration {
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile](path)

  import dbConfig.profile.api._

  def withRollback(testCode: => DBIO[Any]) {
    val testResult: Try[Any] = Await.result(dbConfig.db.run(
      (testCode >>
        DBIO.failed(new TestRollBackException)).transactionally.asTry), 2 seconds)
    testResult match {
      case Success(_) =>
      case Failure(_: TestRollBackException) =>
      case Failure(e: TestFailedException) => throw e
      case Failure(e) => fail(e)
    }
  }

  override def beforeAll(): Unit = {
    migrate(dbConfig.config)
  }

  override def afterAll(): Unit = {
    dbConfig.db.close()
  }

  private class TestRollBackException extends RuntimeException

}
