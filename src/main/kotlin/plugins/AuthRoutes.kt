package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.models.User
import org.example.models.UserCredentials
import org.example.services.AuthService

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val user = call.receive<User>()
            val result = authService.register(user)
            call.respond(HttpStatusCode.Created, result)
        }

        post("/login") {
            val credentials = call.receive<UserCredentials>()
            val result = authService.login(credentials)
            call.respond(HttpStatusCode.OK, result)
        }

        get("/validate") {
            val authHeader = call.request.header(HttpHeaders.Authorization) ?: return@get call.respond(
                HttpStatusCode.Unauthorized, "Missing Authorization header"
            )

            if (!authHeader.startsWith("Bearer ")) {
                return@get call.respond(HttpStatusCode.Unauthorized, "Invalid Authorization header format")
            }

            val token = authHeader.substring(7)
            val user = authService.validateToken(token)
            call.respond(HttpStatusCode.OK, user)
        }
    }
}