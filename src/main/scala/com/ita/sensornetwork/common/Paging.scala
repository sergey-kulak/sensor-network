package com.ita.sensornetwork.common

case class PageRequest(pageNumber: Int, length: Int, sort: Sort = Sort(PageRequest.DefaultSortField, SortDirection.Asc)) {
  def startIndex: Int = pageNumber * length

  def pageCount(totalCount: Int): Int = (totalCount.toFloat / length).ceil.toInt
}

object PageRequest {
  val IdField = "id"
  val DefaultSortField = IdField
}

class SortDirection(code: String)

object SortDirection {

  object Asc extends SortDirection("asc")

  object Desc extends SortDirection("desc")

}

case class Sort(field: String, sortDirection: SortDirection)

case class Page[T](content: Seq[T], pageNumber: Int, totalPages: Int, totalItems: Int)

object Page {
  def empty[T] = Page[T](content = Seq.empty, 0, 0, 0)
}
