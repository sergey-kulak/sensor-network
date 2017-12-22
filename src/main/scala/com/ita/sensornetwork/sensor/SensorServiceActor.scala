package com.ita.sensornetwork.sensor

import akka.actor.{Actor, ActorLogging, Props}
import com.ita.sensornetwork.sensor.SensorServiceActor.RegisterSensor

object SensorServiceActor {
  def props = Props(new SensorServiceActor)

  case class RegisterSensor(serialNumber: String)

}

class SensorServiceActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case RegisterSensor(serialNumber) =>
  }
}
