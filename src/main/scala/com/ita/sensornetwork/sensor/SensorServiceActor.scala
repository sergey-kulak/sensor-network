package com.ita.sensornetwork.sensor

import akka.actor.{Actor, ActorLogging, Props}
import com.ita.sensornetwork.sensor.SensorServiceActor.RegisterSensor

object SensorServiceActor {
  def props(sensorDao: SensorDao) = Props(new SensorServiceActor(sensorDao))

  case class RegisterSensor(serialNumber: String)

}

class SensorServiceActor(val sensorDao: SensorDao) extends Actor with ActorLogging {
  override def receive: Receive = {
    case RegisterSensor(serialNumber) =>
  }
}
