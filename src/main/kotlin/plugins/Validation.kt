package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.example.models.dtos.*

fun Application.configureValidation() {
    install(RequestValidation) {
        // Auth validation
        validate<RegisterUserDto> { dto ->
            val errors = AuthValidation.validateRegisterUser(dto)
            if (errors.isNotEmpty()) {
                ValidationResult.Invalid(errors.joinToString(", ") { it.message })
            } else {
                ValidationResult.Valid
            }
        }

        validate<LoginDto> { dto ->
            val errors = AuthValidation.validateLogin(dto)
            if (errors.isNotEmpty()) {
                ValidationResult.Invalid(errors.joinToString(", ") { it.message })
            } else {
                ValidationResult.Valid
            }
        }

        // Review validation
        validate<CreateReviewDto> { dto ->
            val errors = ReviewValidation.validateCreateReview(dto)
            if (errors.isNotEmpty()) {
                ValidationResult.Invalid(errors.joinToString(", ") { it.message })
            } else {
                ValidationResult.Valid
            }
        }

        validate<UpdateReviewDto> { dto ->
            val errors = ReviewValidation.validateUpdateReview(dto)
            if (errors.isNotEmpty()) {
                ValidationResult.Invalid(errors.joinToString(", ") { it.message })
            } else {
                ValidationResult.Valid
            }
        }
    }
    
    // Note: The ValidationException handling is now in StatusPages.kt
} 