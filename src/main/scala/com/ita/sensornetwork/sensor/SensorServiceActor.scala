package com.ita.sensornetwork.sensor

import akka.actor.{Actor, ActorLogging, Props}

object SensorServiceActor {
  def props(sensorDao: SensorDao) = Props(new SensorServiceActor(sensorDao))

  //case class RegisterSensor(serialNumber: String)

}

class SensorServiceActor(val sensorDao: SensorDao) extends Actor with ActorLogging {
  override def receive: Receive = {
    //case RegisterSensor(serialNumber) =>
    case _ =>
  }
}
