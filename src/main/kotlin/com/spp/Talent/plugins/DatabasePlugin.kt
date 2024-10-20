package com.spp.Talent.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
//import io.ktor.application.*
import io.ktor.http.ContentType
import org.jetbrains.exposed.sql.Database

fun configureDatabase() {
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://photon-medical.com:3306/card_game"  // Replace with your MySQL URL
        driverClassName = "com.mysql.cj.jdbc.Driver"  // MySQL driver
        username = "ktor_user"  // MySQL user
        password = "MarkTalent@StrongPassword"  // MySQL password
        maximumPoolSize = 10  // Maximum number of connections in the pool
        isAutoCommit = false  // Transactions are handled manually
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"  // MySQL recommended transaction isolation
    }

    // Create the Hikari DataSource and connect Exposed to it
    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    println("Connected to MySQL database successfully!")
}