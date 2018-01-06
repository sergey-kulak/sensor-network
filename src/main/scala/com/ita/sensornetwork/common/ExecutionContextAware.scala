package com.ita.sensornetwork.common

import scala.concurrent.ExecutionContext

trait ExecutionContextAware {
  implicit def executionContext: ExecutionContext
}
