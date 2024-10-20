package com.spp.Talent.tables


import org.jetbrains.exposed.sql.Table

object PlayersTable : Table("players") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}
