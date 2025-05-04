package org.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import org.example.models.UserResponse
import java.util.Date

class JwtService {
    private val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }
    
    private val issuer = dotenv["JWT_ISSUER"] ?: "mechanic-rating-app"
    private val audience = dotenv["JWT_AUDIENCE"] ?: "mechanic-rating-users"
    private val secret = dotenv["JWT_SECRET"] ?: "default-development-secret-do-not-use-in-production"
    private val validity = dotenv["JWT_VALIDITY"]?.toLongOrNull() ?: 36_000_000 // 10 hours in milliseconds

    fun generateToken(user: UserResponse): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", user.id)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + validity))
            .sign(Algorithm.HMAC256(secret))
    }

    fun getUserIdFromToken(token: String): String {
        val verifier = JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

        val jwt = verifier.verify(token)
        return jwt.getClaim("userId").asString()
    }
}