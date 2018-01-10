package com.ita.sensornetwork.sensor.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{get, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import com.ita.sensornetwork.common._
import com.ita.sensornetwork.sensor._
import com.ita.sensornetwork.sensor.dao.SensorDaoComponent

trait SensorRestApiComponent extends SprayJsonSupport with JsonProtocol {
  this: SensorDaoComponent =>

  val DefaultPageNumber = 0
  val DefaultPageLength = 20

  implicit val sensorFormat = jsonFormat4(Sensor)
  implicit val registerSensorFormat = jsonFormat3(RegisterSensor)
  implicit val createDataFormat = jsonFormat3(CreateSensorData)
  implicit val sensorDataFormat = jsonFormat5(SensorData)
  implicit val fullSensorDataFormat = jsonFormat5(FullSensorData)
  implicit val pageFormat = jsonFormat4(Page[FullSensorData])
  implicit val sortFormat = jsonFormat2(Sort)
  implicit val pageRequestFormat = jsonFormat3(PageRequest)

  def sensorRestApi: SensorRestApi = new SensorRestApi {}

  trait SensorRestApi {

    def routes = basicAuth() { _ =>
      pathPrefix("sensors") {
        pathPrefix(IntNumber) { id =>
          path("data") {
            pathEndOrSingleSlash {
              get {
                parameters("pageNumber".as[Int] ? DefaultPageNumber, "length".as[Int] ? DefaultPageLength, "sortField".?, "direction".?) {
                  (pageNumber, length, sortFieldOpt, directionOpt) =>
                    val sortDirection = directionOpt.flatMap(SortDirection.withNameInsensitiveOption).getOrElse(SortDirection.Asc)
                    val sortField = sortFieldOpt.getOrElse(PageRequest.DefaultSortField)
                    val pageRequest = PageRequest(pageNumber, length, Sort(sortField, sortDirection))

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
          } ~ pathEndOrSingleSlash {
            get {
              onSuccess(sensorDao.findById(id)) {
                case Some(sensor) => complete(sensor)
                case None => complete(StatusCodes.NotFound)
              }
            }
          }
        } ~ pathEndOrSingleSlash {
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

  def basicAuth()(path: String => Route) = {
    authenticateBasic(realm = "secure site", userPassAuthenticator) { us: String =>
      path(us)
    }
  }

  def userPassAuthenticator(credentials: Credentials): Option[String] = {
    credentials match {
      case p@Credentials.Provided(id) if p.verify("pass") => Some(id)
      case _ => None

    }
  }

}

