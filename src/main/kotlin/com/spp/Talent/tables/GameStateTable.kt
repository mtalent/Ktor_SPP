package com.spp.Talent.tables


import org.jetbrains.exposed.sql.Table

object GameStateTable : Table("game_state") {
    val id = integer("id").autoIncrement()
    val gameId = integer("game_id").references(GamesTable.gameId)
    val playerId = integer("player_id").references(PlayersTable.id)
    val card1 = varchar("card1", 50)
    val card2 = varchar("card2", 50)
    val card3 = varchar("card3", 50)
    val card4 = varchar("card4", 50)
    val card5 = varchar("card5", 50)

    override val primaryKey = PrimaryKey(id)
}
