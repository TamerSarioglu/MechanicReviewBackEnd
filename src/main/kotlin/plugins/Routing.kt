package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.repositories.MechanicRepository
import org.example.repositories.ReviewRepository
import org.example.repositories.UserRepository
import org.example.services.AuthService
import org.example.services.JwtService

fun Application.configureRouting() {
    val userRepository = UserRepository()
    val mechanicRepository = MechanicRepository()
    val reviewRepository = ReviewRepository()

    val jwtService = JwtService()
    val authService = AuthService(userRepository, jwtService)

    routing {
        route("/api") {
            get("/health") {
                call.respond(mapOf("status" to "ok"))
            }

            authRoutes(authService)
            mechanicRoutes(mechanicRepository)
            reviewRoutes(reviewRepository, jwtService)
        }
    }
}