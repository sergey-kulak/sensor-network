package com.ita.sensornetwork

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ClientMain extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://tut.by"))
  val result = Await.result(responseFuture, 10 seconds).entity.toStrict(3.seconds)
  println(result)
}
