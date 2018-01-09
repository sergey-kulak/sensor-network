package com.ita.sensornetwork.sensor.rest

import java.time.LocalDateTime

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, Location}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ita.sensornetwork.common._
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDaoComponent
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future

class SensorRestApiComponentSpec extends WordSpecLike with Matchers
  with ScalatestRouteTest with SensorRestApiComponent with SensorDaoComponent with MockFactory {

  val Auth = Authorization(BasicHttpCredentials("user", "pass"))

  val sensorDaoMock = mock[SensorDao]

  def sensorDao = sensorDaoMock

  def routes = Route.seal {
    sensorRestApi.routes
  }

  "Sensor Rest Api" should {
    "support basic auth and reject requests with it" in {
      Get("/sensors") ~> routes ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return all sensors" in {
      val sensors = Seq(Sensor("s1", id = 1))
      (sensorDao.findAll _).expects().returning(Future(sensors))

      Get("/sensors").withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[Sensor]] shouldEqual sensors
      }
    }

    "return a sensor by id if it exists" in {
      val sensor = Sensor("s1", id = 1)
      (sensorDao.findById _).expects(sensor.id).returning(Future(Some(sensor)))

      Get(s"/sensors/${sensor.id}").withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Sensor] shouldEqual sensor
      }
    }

    "return not found if sensor doesn't exist" in {
      val id = 1
      (sensorDao.findById _).expects(id).returns(Future(None))

      Get(s"/sensors/${id}").withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "register a sensor by post request" in {
      val rSensor = RegisterSensor("s1", LocalDateTime.now().minusDays(1),
        Set(MeasurableParameter.NoiseLevel, MeasurableParameter.Temperature))
      val id = 100
      (sensorDao.register _).expects(rSensor).returns(
        Future(Sensor(rSensor.serialNumber, rSensor.registrationDate, rSensor.measurableParameters, id)))

      Post("/sensors", rSensor).withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
        header[Location] shouldEqual Some(Location(s"/sensors/${id}"))
      }
    }

    "return sensor data" in {
      val sensor = Sensor("s1", id = 1)

      val pageNumber = 1
      val length = 10
      val sortField = SensorDataField.Time
      val direction = SortDirection.Desc
      val pageRequest = PageRequest(pageNumber, length, Sort(sortField, direction))

      val expectedFilter = SensorDataFilter(pageRequest, sensorId = Some(sensor.id))
      val sensorData = Seq(FullSensorData(sensor, MeasurableParameter.NoiseLevel, 1, LocalDateTime.now(), 1))
      val page = Page(sensorData, 0, 1, 1)
      (sensorDao.findSensorData _).expects(expectedFilter).returning(Future(page))

      Get(s"/sensors/${sensor.id}/data?pageNumber=${pageNumber}&length=${length}" +
        s"&sortField=${sortField}&direction=${direction.code}").withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Page[FullSensorData]] shouldEqual page
      }
    }

    "returns sensor data with default paging" in {
      val sensorId = 1

      val pageNumber = 0
      val length = 20
      val sortField = PageRequestField.DefaultSortField
      val direction = SortDirection.Asc
      val pageRequest = PageRequest(pageNumber, length, Sort(sortField, direction))

      val expectedFilter = SensorDataFilter(pageRequest, sensorId = Some(sensorId))

      val page = Page(Seq.empty[FullSensorData], 0, 0, 0)
      (sensorDao.findSensorData _).expects(expectedFilter).returning(Future(page))

      Get(s"/sensors/${sensorId}/data").withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Page[FullSensorData]] shouldEqual page
      }
    }

    "record sensor data" in {
      val sensorId = 1
      val createSensorData = CreateSensorData(MeasurableParameter.NoiseLevel, 1)
      val sensorData = SensorData(sensorId, MeasurableParameter.NoiseLevel, 1)
      (sensorDao.saveSensorData _).expects(sensorId, *).returning(Future(sensorData))

      Post(s"/sensors/${sensorId}/data", createSensorData).withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    "reject record sensor data with negative humidity" in {
      case class TestCreateSensorData(measurableParameter: MeasurableParameter,
                                      value: Double,
                                      time: LocalDateTime = LocalDateTime.now())
      implicit val testCreateSensorDataFormat = jsonFormat3(TestCreateSensorData)

      val sensorId = 1
      val createSensorData = TestCreateSensorData(MeasurableParameter.Humidity, -1)

      Post(s"/sensors/${sensorId}/data", createSensorData).withHeaders(Auth) ~> routes ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }
  }
}
