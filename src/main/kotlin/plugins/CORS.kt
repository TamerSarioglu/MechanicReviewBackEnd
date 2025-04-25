package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    // Determine the current environment
    val env = environment.config.property("ktor.environment").getString()
    // Get the config block for the current environment
    val envConfig = environment.config.config("environments.$env")
    // Read CORS properties from the environment-specific block
    val corsConfig = envConfig.config("cors")

    val allowedHosts = corsConfig.property("allowedHosts").getList()
    val allowedSchemes = corsConfig.property("allowedSchemes").getList()

    install(CORS) {
        // Common methods and headers
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true // Allow credentials

        // Configure hosts based on application.conf
        allowedHosts.forEach { host ->
            if (host == "*") {
                anyHost()
            } else {
                // Need to specify schemes for each host
                allowHost(host, schemes = allowedSchemes)
            }
        }
    }
}