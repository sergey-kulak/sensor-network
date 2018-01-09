package com.ita.sensornetwork.common

case class PageRequest(pageNumber: Int,
                       length: Int,
                       sort: Sort = Sort(PageRequestField.DefaultSortField, SortDirection.Asc)) {
  def startIndex: Int = pageNumber * length

  def pageCount(totalCount: Int): Int = (totalCount.toFloat / length).ceil.toInt
}

object PageRequestField {
  val IdField = "id"
  val DefaultSortField = IdField
}

sealed abstract class SortDirection(val code: String)

object SortDirection {

  case object Asc extends SortDirection("asc")

  case object Desc extends SortDirection("desc")

  def values = Array(Asc, Desc)

  def foundByCode(code: String): Option[SortDirection] = values.find(_.code == code)
}

case class Sort(field: String, sortDirection: SortDirection)

case class Page[T](content: Seq[T], pageNumber: Int, totalPages: Int, totalItems: Int)
