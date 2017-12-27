package com.ita.sensornetwork.sensor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, WordSpecLike}
import scala.concurrent.duration._

class SensorServiceActorSpec extends TestKit(ActorSystem("sensorActors"))
  with ImplicitSender with WordSpecLike with Matchers {

  "SensorService actor" should {
    "register sensor" in {
      testActor ! "echo"
      expectMsg(500 millis, "echo")
    }
  }

}
