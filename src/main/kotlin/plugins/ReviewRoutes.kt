package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.models.Review
import org.example.repositories.ReviewRepository
import org.example.services.JwtService

fun Route.reviewRoutes(reviewRepository: ReviewRepository, jwtService: JwtService) {
    route("/reviews") {
        // Create a new review (protected route)
        authenticate("auth-jwt") {
            post {
                val principal = call.principal<JWTPrincipal>() ?: throw BadRequestException("Invalid token")
                val userId = principal.payload.getClaim("userId").asString()

                val review = call.receive<Review>()

                // Ensure the user ID in the token matches the one in the review
                if (review.userId != userId) {
                    throw BadRequestException("User ID in token does not match the one in the review")
                }

                val result = reviewRepository.createReview(review)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        // Get a specific review by ID
        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("Missing review ID")
            val review = reviewRepository.getReviewById(id) ?: throw NotFoundException("Review not found")
            call.respond(HttpStatusCode.OK, review)
        }

        // Get all reviews for a specific mechanic
        get("/mechanic/{mechanicId}") {
            val mechanicId = call.parameters["mechanicId"] ?: throw BadRequestException("Missing mechanic ID")
            val reviews = reviewRepository.getReviewsByMechanicId(mechanicId)
            call.respond(HttpStatusCode.OK, reviews)
        }

        // Get all reviews by a specific user (protected route)
        authenticate("auth-jwt") {
            get("/user") {
                val principal = call.principal<JWTPrincipal>() ?: throw BadRequestException("Invalid token")
                val userId = principal.payload.getClaim("userId").asString()

                val reviews = reviewRepository.getReviewsByUserId(userId)
                call.respond(HttpStatusCode.OK, reviews)
            }
        }
    }
}