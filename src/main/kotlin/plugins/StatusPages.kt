package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.concurrent.TimeoutException
import io.ktor.server.plugins.requestvalidation.*
import javax.naming.AuthenticationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        // 400 Bad Request
        exception<BadRequestException> { call, cause ->
            call.application.log.warn("Bad request: ${cause.message}")
            call.respondText(
                text = cause.message ?: "Bad Request",
                status = HttpStatusCode.BadRequest
            )
        }
        
        // 401 Unauthorized
        exception<AuthenticationException> { call, _ ->
            call.application.log.warn("Authentication failed")
            call.respondText(
                text = "Authentication failed",
                status = HttpStatusCode.Unauthorized
            )
        }
        
        // 403 Forbidden
        status(HttpStatusCode.Forbidden) { call, _ ->
            call.respondText(
                text = "Access denied",
                status = HttpStatusCode.Forbidden
            )
        }
        
        // 404 Not Found
        exception<NotFoundException> { call, cause ->
            call.application.log.warn("Resource not found: ${cause.message}")
            call.respondText(
                text = cause.message ?: "Resource not found",
                status = HttpStatusCode.NotFound
            )
        }
        
        // 408 Request Timeout
        exception<TimeoutException> { call, _ ->
            call.application.log.warn("Request timeout")
            call.respondText(
                text = "Request timeout",
                status = HttpStatusCode.RequestTimeout
            )
        }
        
        // 409 Conflict
        status(HttpStatusCode.Conflict) { call, _ ->
            call.respondText(
                text = "Resource conflict",
                status = HttpStatusCode.Conflict
            )
        }
        
        // 422 Unprocessable Entity
        exception<RequestValidationException> { call, cause ->
            call.application.log.warn("Validation error: ${cause.reasons}")
            call.respondText(
                text = cause.reasons.joinToString(", "),
                status = HttpStatusCode.UnprocessableEntity
            )
        }
        
        // 500 Internal Server Error
        exception<ExposedSQLException> { call, cause ->
            call.application.log.error("Database error", cause)
            call.respondText(
                text = "Database operation failed",
                status = HttpStatusCode.InternalServerError
            )
        }
        
        // Fallback for any other exceptions
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respondText(
                text = "An internal error occurred",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}