package com.ita.sensornetwork.sensor

import org.scalatest.{Matchers, WordSpec}

class SensorTest extends WordSpec with Matchers {

  "Sensors" should {
    "be equal if they have the same ids" in {
      val s1 = Sensor("s1", id = 1)
      val s2 = Sensor("s2", id = 1)

      assert(s1 === s2)
    }
  }

}
