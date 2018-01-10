package com.ita.sensornetwork.common

import enumeratum._

case class PageRequest(pageNumber: Int,
                       length: Int,
                       sort: Sort = Sort(PageRequest.DefaultSortField, SortDirection.Asc)) {
  def startIndex: Int = pageNumber * length

  def pageCount(totalCount: Int): Int = (totalCount.toFloat / length).ceil.toInt
}

object PageRequest extends ((Int, Int, Sort) => PageRequest) {
  val IdField = "id"
  val DefaultSortField = IdField
}

sealed abstract class SortDirection(override val entryName: String) extends EnumEntry

object SortDirection extends Enum[SortDirection] {

  case object Asc extends SortDirection("asc")

  case object Desc extends SortDirection("desc")

  override def values = findValues
}

case class Sort(field: String, sortDirection: SortDirection)

case class Page[T](content: Seq[T], pageNumber: Int, totalPages: Int, totalItems: Int)
