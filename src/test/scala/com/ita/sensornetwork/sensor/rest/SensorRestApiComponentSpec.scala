package com.ita.sensornetwork.sensor.rest

import java.time.LocalDateTime

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ita.sensornetwork.common.Page
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDaoComponent
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future

class SensorRestApiComponentSpec extends WordSpecLike with Matchers
  with ScalatestRouteTest with SensorRestApiComponent with SensorDaoComponent with MockFactory {

  val sensorDaoMock = mock[SensorDao]

  def sensorDao = sensorDaoMock

  def routes = sensorRestApi.routes

  "Sensor Rest Api" should {
    "return all sensors" in {
      val sensors = Seq(Sensor("s1", id = 1))
      (sensorDao.findAll _).expects().returning(Future(sensors))

      Get("/sensors") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[Sensor]] shouldEqual sensors
      }
    }

    "return a sensor by id if it exists" in {
      val sensor = Sensor("s1", id = 1)
      (sensorDao.findById _).expects(sensor.id).returning(Future(Some(sensor)))

      Get(s"/sensors/${sensor.id}") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Sensor] shouldEqual sensor
      }
    }

    "return not found if sensor doesn't exist" in {
      val id = 1
      (sensorDao.findById _).expects(id).returns(Future(None))

      Get(s"/sensors/${id}") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "register a sensor by post request" in {
      val rSensor = RegisterSensor("s1", LocalDateTime.now().minusDays(1),
        Set(MeasurableParameter.NoiseLevel, MeasurableParameter.Temperature))
      val id = 100
      (sensorDao.register _).expects(rSensor).returns(
        Future(Sensor(rSensor.serialNumber, rSensor.registrationDate, rSensor.measurableParameters, id)))

      Post("/sensors", rSensor) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
        header[Location] shouldEqual Some(Location(s"/sensors/${id}"))
      }
    }

    "returns sensor data" in {
      val sensor = Sensor("s1", id = 1)
      val sensorData = Seq(FullSensorData(sensor, MeasurableParameter.NoiseLevel, 1, LocalDateTime.now(), 1))
      val page = Page(sensorData, 0, 1, 1)
      (sensorDao.findSensorData _).expects(*).returning(Future(page))

      Get(s"/sensors/${sensor.id}/data?pageNumber=0&length=10") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Page[FullSensorData]] shouldEqual page
      }
    }

    "record sensor data" in {
      val sensorId = 1
      val createSensorData = CreateSensorData(MeasurableParameter.NoiseLevel, 1)
      val sensorData = SensorData(sensorId, MeasurableParameter.NoiseLevel, 1)
      (sensorDao.saveSensorData _).expects(sensorId, *).returning(Future(sensorData))

      Post(s"/sensors/${sensorId}/data", createSensorData) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }
  }
}
