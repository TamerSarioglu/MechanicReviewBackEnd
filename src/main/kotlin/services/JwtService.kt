package org.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.example.models.UserResponse
import java.util.Date

class JwtService {
    private val issuer = "mechanic-rating-app"
    private val audience = "mechanic-rating-users"
    private val secret = "your-secret-key" // In production, this should be a secure environment variable
    private val validity = 36_000_000 // 10 hours in milliseconds

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