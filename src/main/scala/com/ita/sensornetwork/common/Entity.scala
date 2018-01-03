package com.ita.sensornetwork.common

abstract class Entity[ID] {

  def id: ID

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Entity[ID] => that.id == this.id
      case _ => false
    }
  }

  override def hashCode(): Int = id.hashCode
}

