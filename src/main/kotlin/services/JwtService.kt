package org.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import org.example.models.UserResponse
import java.util.Date

// Constructor now accepts ApplicationConfig
class JwtService(config: ApplicationConfig) {
    // Determine the current environment
    private val env = config.property("ktor.environment").getString()
    // Get the config block for the current environment
    private val envConfig = config.config("environments.$env")
    // Read JWT properties from the environment-specific block
    private val jwtConfig = envConfig.config("jwt")

    private val issuer = jwtConfig.property("issuer").getString()
    private val audience = jwtConfig.property("audience").getString()
    private val secret = jwtConfig.property("secret").getString()
    private val validity = jwtConfig.property("validityMs").getString().toLong()
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(user: UserResponse): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", user.id)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + validity))
            .sign(algorithm)
    }

    // Consider making the verifier accessible if needed elsewhere, or recreate it
    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun getUserIdFromToken(token: String): String {
        val jwt = verifier.verify(token)
        return jwt.getClaim("userId").asString()
    }
}