package com.spp.Talent

import com.spp.Talent.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

fun main() {
    embeddedServer(Netty, port = 80, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    Database.connect(
        "jdbc:mysql://144.126.251.241:3306/card_game",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "ktor_user",
        password = "MarkTalent@StrongPassword"
    )

    configureSerialization()
    configureRouting()
}


object CardsTable : Table("cards") {
    val name = varchar("name", 50)
    val url = varchar("url", 255)
}