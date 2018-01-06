package com.ita.sensornetwork.sensor

import akka.actor.{Actor, ActorLogging, Props}

object SensorServiceActor {
  def props() = Props(new SensorServiceActor())

  //case class RegisterSensor(serialNumber: String)

}

class SensorServiceActor() extends Actor with ActorLogging {
  override def receive: Receive = {
    //case RegisterSensor(serialNumber) =>
    case _ =>
  }
}
