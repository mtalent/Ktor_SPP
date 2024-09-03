package com.spp.Talent.plugins

import io.ktor.http.*
import java.io.File
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondFile(File("/var/www/html/resume.pdf"))
        }

        static("/cards") {
            files("/var/www/html/cards")
        }

        get("/api/cards") {
            // Add your database logic here to retrieve card data
            call.respondText("Hello, World!")
        }
    }
}
