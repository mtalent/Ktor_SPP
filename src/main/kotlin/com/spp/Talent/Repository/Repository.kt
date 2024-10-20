package com.spp.Talent.repository

import com.spp.Talent.tables.GameStateTable
import com.spp.Talent.tables.GamesTable
import com.spp.Talent.tables.PlayersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

class Repository {

    suspend fun startGame(): Int {
        return transaction {
            // Insert the game
           GamesTable.insert {
                it[startTime] = LocalDateTime.now()
                it[winnerPlayerId] = null
            }
            // Retrieve the last inserted game_id (MySQL behavior)
            GamesTable.slice(GamesTable.gameId.max()).selectAll().first()[GamesTable.gameId]
        }
    }

    // Function to add a player to the game
    suspend fun addPlayer(playerName: String): Int {
        return transaction {
            // Insert the player
            PlayersTable.insert {
                it[name] = playerName
            }
            // Retrieve the last inserted player_id (MySQL behavior)
            PlayersTable.slice(PlayersTable.id.max()).selectAll().first()[PlayersTable.id]
        }
    }

    // Function to deal cards to a player
    suspend fun dealCards(gameId: Int, playerId: Int): List<String> {
        // Step 1: Generate 5 random cards (you can replace this logic with your actual card generation logic)
        val suits = listOf("hearts", "diamonds", "clubs", "spades")
        val values = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace")

        val deck = suits.flatMap { suit -> values.map { value -> "$value of $suit" } }.shuffled()
        val dealtCards = deck.take(5)

        // Step 2: Insert the dealt cards into the GameStateTable
        transaction {
            GameStateTable.insert {
                it[GameStateTable.gameId] = gameId
                it[GameStateTable.playerId] = playerId
                it[card1] = dealtCards[0]
                it[card2] = dealtCards[1]
                it[card3] = dealtCards[2]
                it[card4] = dealtCards[3]
                it[card5] = dealtCards[4]
            }
        }

        // Step 3: Return the dealt cards
        return dealtCards
    }


    // Function to post the winner
    suspend fun postWinner(gameId: Int, winnerPlayerId: Int?) {
        transaction {
            GamesTable.update({ GamesTable.gameId eq gameId }) {
                it[endTime] = LocalDateTime.now()
                if (winnerPlayerId != null) {
                    it[GamesTable.winnerPlayerId] = winnerPlayerId  // Set the winner ID if it's not null
                } else {
                    it[GamesTable.winnerPlayerId] = null  // Handle null case
                }
            }
        }
    }

    // Function to retrieve game state
    suspend fun getPlayerGameState(gameId: Int, playerId: Int): ResultRow? {
        return transaction {
            GameStateTable.select {
                (GameStateTable.gameId eq gameId) and (GameStateTable.playerId eq playerId)
            }.singleOrNull()
        }
    }

}
