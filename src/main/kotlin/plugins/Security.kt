package org.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    // Determine the current environment
    val env = environment.config.property("ktor.environment").getString()
    // Get the config block for the current environment
    val envConfig = environment.config.config("environments.$env")

    // Read JWT configuration from the environment-specific block
    val jwtConfig = envConfig.config("jwt")
    val secret = jwtConfig.property("secret").getString()
    val issuer = jwtConfig.property("issuer").getString()
    val audience = jwtConfig.property("audience").getString()
    val realm = "mechanic-rating-app" // Keep realm or externalize if needed

    authentication {
        jwt("auth-jwt") {
            this.realm = realm // Use the variable
            verifier(
                JWT.require(Algorithm.HMAC256(secret)) // Use secret from config
                    .withIssuer(issuer) // Use issuer from config
                    .withAudience(audience) // Use audience from config
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}