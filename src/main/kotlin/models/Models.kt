package org.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String? = null, // Not returned in responses
    val fullName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class Mechanic(
    val id: String? = null,
    val name: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val phone: String,
    val email: String? = null,
    val website: String? = null,
    val specialties: List<String> = emptyList(),
    val operatingHours: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class Review(
    val id: String? = null,
    val userId: String,
    val mechanicId: String,
    val rating: Int, // 1-5 stars
    val comment: String,
    val serviceType: String? = null,
    val serviceDate: String? = null,
    val pricePaid: Double? = null,
    val priceRating: Int? = null, // 1-5 stars for price fairness
    val qualityRating: Int? = null, // 1-5 stars for work quality
    val serviceRating: Int? = null, // 1-5 stars for customer service
    val images: List<String> = emptyList(), // URLs to uploaded images
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ReviewWithUserDetails(
    val id: String,
    val mechanicId: String,
    val username: String,
    val rating: Int,
    val comment: String,
    val serviceType: String?,
    val serviceDate: String?,
    val pricePaid: Double?,
    val priceRating: Int?,
    val qualityRating: Int?,
    val serviceRating: Int?,
    val images: List<String>,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class MechanicWithRating(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val phone: String,
    val email: String?,
    val website: String?,
    val specialties: List<String>,
    val operatingHours: String?,
    val averageRating: Double,
    val totalReviews: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ErrorResponse(
    val statusCode: Int,
    val message: String
)