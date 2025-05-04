package org.example.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewDto(
    val mechanicId: String,
    val rating: Int, // 1-5 stars
    val comment: String,
    val serviceType: String? = null,
    val serviceDate: String? = null,
    val pricePaid: Double? = null,
    val priceRating: Int? = null, // 1-5 stars for price fairness
    val qualityRating: Int? = null, // 1-5 stars for work quality
    val serviceRating: Int? = null, // 1-5 stars for customer service
    val images: List<String> = emptyList() // URLs to uploaded images
)

@Serializable
data class UpdateReviewDto(
    val rating: Int? = null,
    val comment: String? = null,
    val serviceType: String? = null,
    val serviceDate: String? = null,
    val pricePaid: Double? = null,
    val priceRating: Int? = null,
    val qualityRating: Int? = null,
    val serviceRating: Int? = null,
    val images: List<String>? = null
)

// Validation errors
sealed class ReviewValidationError(val message: String) {
    class InvalidRating : ReviewValidationError("Rating must be between 1 and 5")
    class InvalidPriceRating : ReviewValidationError("Price rating must be between 1 and 5")
    class InvalidQualityRating : ReviewValidationError("Quality rating must be between 1 and 5")
    class InvalidServiceRating : ReviewValidationError("Service rating must be between 1 and 5")
    class CommentRequired : ReviewValidationError("Comment is required")
    class MechanicIdRequired : ReviewValidationError("Mechanic ID is required")
    class InvalidPricePaid : ReviewValidationError("Price paid must be a positive number")
}

// Validation functions
object ReviewValidation {
    fun validateRating(rating: Int?): ReviewValidationError? {
        if (rating != null && (rating < 1 || rating > 5)) return ReviewValidationError.InvalidRating()
        return null
    }
    
    fun validatePriceRating(rating: Int?): ReviewValidationError? {
        if (rating != null && (rating < 1 || rating > 5)) return ReviewValidationError.InvalidPriceRating()
        return null
    }
    
    fun validateQualityRating(rating: Int?): ReviewValidationError? {
        if (rating != null && (rating < 1 || rating > 5)) return ReviewValidationError.InvalidQualityRating()
        return null
    }
    
    fun validateServiceRating(rating: Int?): ReviewValidationError? {
        if (rating != null && (rating < 1 || rating > 5)) return ReviewValidationError.InvalidServiceRating()
        return null
    }
    
    fun validateComment(comment: String?): ReviewValidationError? {
        if (comment.isNullOrBlank()) return ReviewValidationError.CommentRequired()
        return null
    }
    
    fun validateMechanicId(mechanicId: String?): ReviewValidationError? {
        if (mechanicId.isNullOrBlank()) return ReviewValidationError.MechanicIdRequired()
        return null
    }
    
    fun validatePricePaid(pricePaid: Double?): ReviewValidationError? {
        if (pricePaid != null && pricePaid <= 0) return ReviewValidationError.InvalidPricePaid()
        return null
    }
    
    fun validateCreateReview(dto: CreateReviewDto): List<ReviewValidationError> {
        val errors = mutableListOf<ReviewValidationError>()
        
        validateMechanicId(dto.mechanicId)?.let { errors.add(it) }
        validateRating(dto.rating)?.let { errors.add(it) }
        validateComment(dto.comment)?.let { errors.add(it) }
        validatePriceRating(dto.priceRating)?.let { errors.add(it) }
        validateQualityRating(dto.qualityRating)?.let { errors.add(it) }
        validateServiceRating(dto.serviceRating)?.let { errors.add(it) }
        validatePricePaid(dto.pricePaid)?.let { errors.add(it) }
        
        return errors
    }
    
    fun validateUpdateReview(dto: UpdateReviewDto): List<ReviewValidationError> {
        val errors = mutableListOf<ReviewValidationError>()
        
        validateRating(dto.rating)?.let { errors.add(it) }
        if (dto.comment != null) validateComment(dto.comment)?.let { errors.add(it) }
        validatePriceRating(dto.priceRating)?.let { errors.add(it) }
        validateQualityRating(dto.qualityRating)?.let { errors.add(it) }
        validateServiceRating(dto.serviceRating)?.let { errors.add(it) }
        validatePricePaid(dto.pricePaid)?.let { errors.add(it) }
        
        return errors
    }
} 