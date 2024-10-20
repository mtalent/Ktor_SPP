package com.spp.Talent.routes


import com.spp.Talent.evaluate.evaluateHand
import com.spp.Talent.model.Card
import com.spp.Talent.model.DealCardsRequest
import com.spp.Talent.repository.Repository
import com.spp.Talent.routes.*
import com.spp.Talent.tables.GameStateTable
import com.spp.Talent.tables.GamesTable
import com.spp.Talent.tables.PlayersTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.netty.util.internal.TypeParameterMatcher.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

fun Route.gameRoutes(repository: Repository) {

    post("/register-player") {
        val playerName = call.parameters["name"]
        if (playerName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Player name cannot be empty")
            return@post
        }

        // Ensure no duplicate player names
        val playerId = transaction {
            // Check if the player already exists
            PlayersTable.select { PlayersTable.name eq playerName }
                .singleOrNull()?.get(PlayersTable.id)
                ?: run {
                    // Insert player manually without using insertAndGetId
                    PlayersTable.insert {
                        it[name] = playerName
                    }

                    // Get the last inserted player ID
                    PlayersTable.slice(PlayersTable.id.max())
                        .selectAll()
                        .single()[PlayersTable.id]
                }
        }

        call.respond(mapOf("playerId" to playerId))
    }



    // Start a new game
    post("/start-game") {
        val gameId = withContext(Dispatchers.IO) {
            repository.startGame()
        }
        call.respond(mapOf("gameId" to gameId))
    }

    // Add a player
    post("/add-player") {
        val playerName = call.receive<String>() // Assuming player name is sent in the request
        val playerId = withContext(Dispatchers.IO) {
            repository.addPlayer(playerName)
        }
        call.respond(mapOf("playerId" to playerId))
    }

    // Deal cards to a player
    post("/deal-cards/{playerName}") {
        val playerName = call.parameters["playerName"]
        if (playerName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Player name cannot be empty")
            return@post
        }

        // Step 1: Retrieve playerId using playerName
        val playerId = transaction {
            PlayersTable.select { PlayersTable.name eq playerName }
                .singleOrNull()?.get(PlayersTable.id)
        }

        if (playerId == null) {
            call.respond(HttpStatusCode.NotFound, "Player not found")
            return@post
        }

        // Step 2: Find the active game ID for this player
        val gameId = transaction {
            // Assuming the most recent game is the active game
            GameStateTable.select { GameStateTable.playerId eq playerId }
                .orderBy(GameStateTable.gameId, SortOrder.DESC)
                .limit(1)
                .singleOrNull()?.get(GameStateTable.gameId)
        }

        if (gameId == null) {
            call.respond(HttpStatusCode.NotFound, "No active game found for player $playerName")
            return@post
        }

        // Step 3: Deal cards using playerId and gameId
        repository.dealCards(gameId, playerId)
        call.respond(HttpStatusCode.OK, "Cards dealt to $playerName")
    }


    // Post the winner
    post("/post-winner") {
        val gameId = call.parameters["gameId"]?.toIntOrNull()
        if (gameId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid game ID")
            return@post
        }

        // Step 1: Retrieve hands of all players in the game
        val playerHands = transaction {
            GameStateTable.select { GameStateTable.gameId eq gameId }.associate { resultRow ->
                resultRow[GameStateTable.playerId] to listOf(
                    Card(resultRow[GameStateTable.card1], "suit"),  // Adjust card suit handling
                    Card(resultRow[GameStateTable.card2], "suit"),
                    Card(resultRow[GameStateTable.card3], "suit"),
                    Card(resultRow[GameStateTable.card4], "suit"),
                    Card(resultRow[GameStateTable.card5], "suit")
                )
            }
        }

        if (playerHands.isEmpty()) {
            call.respond(HttpStatusCode.NotFound, "No players found for this game")
            return@post
        }

        // Step 2: Evaluate each player's hand
        val handScores = playerHands.mapValues { (_, hand) -> evaluateHand(hand) }

        // Step 3: Determine the winner (player with the highest score)
        val (winnerPlayerId, _) = handScores.maxByOrNull { it.value } ?: run {
            call.respond(HttpStatusCode.InternalServerError, "Error determining the winner")
            return@post
        }

        // Step 4: Update the GamesTable to set the winnerPlayerId
        transaction {
            GamesTable.update({ GamesTable.gameId eq gameId }) {
                it[GamesTable.winnerPlayerId] = winnerPlayerId
                it[endTime] = LocalDateTime.now()
            }
        }

        // Step 5: Respond with the winner
        call.respond(mapOf("winnerPlayerId" to winnerPlayerId))
    }


    // Get game state
// Get the game state for a specific player
    get("/game-state/{gameId}/{playerId}") {
        val gameId = call.parameters["gameId"]?.toIntOrNull()
        val playerId = call.parameters["playerId"]?.toIntOrNull()

        if (gameId == null || playerId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid game or player ID")
            return@get
        }

        // Retrieve only the game state for this player
        val playerGameState = withContext(Dispatchers.IO) {
            repository.getPlayerGameState(gameId, playerId)
        }

        if (playerGameState == null) {
            call.respond(HttpStatusCode.NotFound, "No game state found for player $playerId in game $gameId")
        } else {
            call.respond(playerGameState)
        }
    }

}