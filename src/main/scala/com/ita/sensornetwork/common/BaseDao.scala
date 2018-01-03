package com.ita.sensornetwork.common

import slick.lifted.ColumnOrdered

trait BaseDao extends DbConfigAware with CustomColumnTypes {

  import dbConfig.profile.api._

  protected def insertWithId[E <: Entity[Long]](tableQuery: TableQuery[_ <: EntityWithLongTable[E]]) = {
    (tableQuery returning tableQuery.map(_.id)).into[E](_: (E, Long) => E)
  }

  /**
    * Filter the list of optional conditions and union only available ones with and
    *
    * @param conditions
    * @return
    */
  protected def joinAnd(conditions: Option[Rep[Boolean]]*) = {
    conditions.flatten.fold(true.bind)(_ && _)
  }

  protected def buildSort(sort: Sort, mapper: String => ColumnOrdered[_]): ColumnOrdered[_] = {
    val sortField: ColumnOrdered[_] = mapper(sort.field)
    sort.sortDirection match {
      case SortDirection.Asc => sortField.asc
      case SortDirection.Desc => sortField.desc
    }
  }

  protected abstract class EntityWithLongTable[E <: Entity[Long]](tag: Tag, tableName: String) extends Table[E](tag, tableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

}
