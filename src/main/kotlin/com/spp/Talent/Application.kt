package com.spp.Talent

import com.spp.Talent.repository.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import com.spp.Talent.plugins.*
import com.spp.Talent.routes.gameRoutes
import io.ktor.server.routing.routing

fun main() {
    // Create the SSL context
    val keyStore = loadKeyStore(
        keystoreFile = File("/etc/ssl/keystore.jks"),
        keystorePassword = "Oo022496*"
    )

    // Define the environment for Ktor
    val environment = applicationEngineEnvironment {
        module {
            // Call the module function where you set up routing and other features
            module()
        }

        // HTTP connector (optional)
        connector {
            port = 80
            host = "0.0.0.0"
        }

        // HTTPS connector with SSL configuration
        sslConnector(
            keyStore = keyStore,  // Correctly passing the KeyStore object
            keyAlias = "mydomain",
            keyStorePassword = { "Oo022496*".toCharArray() },
            privateKeyPassword = { "Oo022496*".toCharArray() }
        ) {
            port = 443
            host = "0.0.0.0"
        }
    }

    // Start the Ktor server
    embeddedServer(Netty, environment).start(wait = true)
}

// This function loads the KeyStore from the file system
fun loadKeyStore(keystoreFile: File, keystorePassword: String): KeyStore {
    val keyStore = KeyStore.getInstance("JKS").apply {
        load(keystoreFile.inputStream(), keystorePassword.toCharArray())
    }
    return keyStore
}

// This is your module function where you configure routing and serialization
fun Application.module() {
    val repository = Repository()
    configureDatabase()
    configureSerialization()
    configureRouting()

    routing {
        gameRoutes(repository)  // Register the game routes
    }


}