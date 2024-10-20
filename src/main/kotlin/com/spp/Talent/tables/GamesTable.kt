package com.spp.Talent.tables


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime


object GamesTable : Table("games") {
    val gameId = integer("game_id").autoIncrement()
    val startTime = datetime("start_time") // Use Exposed's datetime type
    val endTime = datetime("end_time").nullable()
    val winnerPlayerId = integer("winner_player_id").nullable()

    override val primaryKey = PrimaryKey(gameId)
}

