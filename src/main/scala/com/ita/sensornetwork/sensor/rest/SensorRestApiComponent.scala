package com.ita.sensornetwork.sensor.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{get, _}
import com.ita.sensornetwork.common.{JsonProtocol, Page, PageRequest}
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDaoComponent

trait SensorRestApiComponent extends SprayJsonSupport with JsonProtocol {
  this: SensorDaoComponent =>

  implicit val sensorFormat = jsonFormat4(Sensor)
  implicit val registerSensorFormat = jsonFormat3(RegisterSensor)
  implicit val createDataFormat = jsonFormat3(CreateSensorData)
  implicit val sensorDataFormat = jsonFormat5(SensorData)
  implicit val fullSensorDataFormat = jsonFormat5(FullSensorData)
  implicit val pageFormat = jsonFormat4(Page[FullSensorData])

  def sensorRestApi: SensorRestApi = new SensorRestApi {}

  trait SensorRestApi {

    def routes = pathPrefix("sensors") {
      pathPrefix(IntNumber) { id =>
        path("data") {
          pathEnd {
            get {
              parameter("pageNumber".as[Int], "length".as[Int]) { (pageNumber, length) =>
                val pageRequest = PageRequest(pageNumber, length)
                val filter = SensorDataFilter(pageRequest, sensorId = Some(id))
                onSuccess(sensorDao.findSensorData(filter))(complete(_))
              }
            } ~ post {
              entity(as[CreateSensorData]) { sData =>
                onSuccess(sensorDao.saveSensorData(id, sData)) { _ =>
                  complete(StatusCodes.Created)
                }
              }
            }
          }
        } ~ pathEnd {
          get {
            onSuccess(sensorDao.findById(id)) {
              case Some(sensor) => complete(sensor)
              case None => complete(StatusCodes.NotFound)
            }
          }
        }
      } ~ pathEnd {
        get {
          onSuccess(sensorDao.findAll())(complete(_))
        } ~ post {
          entity(as[RegisterSensor]) { rSensor =>
            onSuccess(sensorDao.register(rSensor))(sensor => {
              complete(HttpResponse(status = StatusCodes.Created, headers = List(Location(s"/sensors/${sensor.id}"))))
            })
          }
        }
      }

    }

  }

}


