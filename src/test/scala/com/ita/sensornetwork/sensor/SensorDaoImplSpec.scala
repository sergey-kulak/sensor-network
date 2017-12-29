package com.ita.sensornetwork.sensor

import java.time.LocalDateTime

import com.ita.sensornetwork.SlickTestKit
import org.scalatest.{Matchers, WordSpecLike}

class SensorDaoImplSpec extends SlickTestKit("sensor-network-db") with WordSpecLike with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  val sensorDao = new SensorDaoImpl(dbConfig)

  "SensorDaoImpl" should {
    "register a new sensor" in withRollback {
      val expectedSerialNumber = "123"
      val expectedRegDate = LocalDateTime.now()

      val registerDto = RegisterSensor(expectedSerialNumber, expectedRegDate)

      sensorDao.registerAction(registerDto)
        .map { sensor =>
          assert(sensor.id.isDefined)
          assert(sensor.serialNumber === expectedSerialNumber)
          assert(sensor.registrationDate === expectedRegDate)
        }
    }

    "return all sensors" in withRollback {
      val expectedSerialNumber = "123"
      val expectedRegDate = LocalDateTime.now()

      val registerDto = RegisterSensor(expectedSerialNumber, expectedRegDate)
      sensorDao.registerAction(registerDto)
        .zip(sensorDao.findAllAction())
        .map { res =>
          val sensor = res._1
          val sensors = res._2
          assert(sensors.nonEmpty)
          assert(sensors.contains(sensor))
        }
    }
  }
}
