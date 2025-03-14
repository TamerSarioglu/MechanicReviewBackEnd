package org.example.repositories

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.database.DatabaseFactory.dbQuery
import org.example.database.ReviewsTable
import org.example.database.UsersTable
import org.example.models.Review
import org.example.models.ReviewWithUserDetails
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ReviewRepository {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createReview(review: Review): Review = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        ReviewsTable.insert {
            it[ReviewsTable.id] = id
            it[userId] = review.userId
            it[mechanicId] = review.mechanicId
            it[rating] = review.rating
            it[comment] = review.comment
            it[serviceType] = review.serviceType
            it[serviceDate] = review.serviceDate
            it[pricePaid] = review.pricePaid
            it[priceRating] = review.priceRating
            it[qualityRating] = review.qualityRating
            it[serviceRating] = review.serviceRating
            it[images] = if (review.images.isNotEmpty()) json.encodeToString(review.images) else null
            it[createdAt] = now
            it[updatedAt] = now
        }

        review.copy(
            id = id,
            createdAt = now.format(formatter),
            updatedAt = now.format(formatter)
        )
    }

    suspend fun getReviewById(id: String): Review? = dbQuery {
        ReviewsTable.select { ReviewsTable.id eq id }
            .mapNotNull { toReview(it) }
            .singleOrNull()
    }

    suspend fun getReviewsByMechanicId(mechanicId: String): List<ReviewWithUserDetails> = dbQuery {
        ReviewsTable.join(UsersTable, JoinType.INNER, ReviewsTable.userId, UsersTable.id)
            .select { ReviewsTable.mechanicId eq mechanicId }
            .mapNotNull { toReviewWithUserDetails(it) }
    }

    suspend fun getReviewsByUserId(userId: String): List<Review> = dbQuery {
        ReviewsTable.select { ReviewsTable.userId eq userId }
            .mapNotNull { toReview(it) }
    }

    private fun toReview(row: ResultRow): Review =
        Review(
            id = row[ReviewsTable.id],
            userId = row[ReviewsTable.userId],
            mechanicId = row[ReviewsTable.mechanicId],
            rating = row[ReviewsTable.rating],
            comment = row[ReviewsTable.comment],
            serviceType = row[ReviewsTable.serviceType],
            serviceDate = row[ReviewsTable.serviceDate],
            pricePaid = row[ReviewsTable.pricePaid],
            priceRating = row[ReviewsTable.priceRating],
            qualityRating = row[ReviewsTable.qualityRating],
            serviceRating = row[ReviewsTable.serviceRating],
            images = try {
                row[ReviewsTable.images]?.let { json.decodeFromString<List<String>>(it) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            createdAt = row[ReviewsTable.createdAt].format(formatter),
            updatedAt = row[ReviewsTable.updatedAt].format(formatter)
        )

    private fun toReviewWithUserDetails(row: ResultRow): ReviewWithUserDetails =
        ReviewWithUserDetails(
            id = row[ReviewsTable.id],
            mechanicId = row[ReviewsTable.mechanicId],
            username = row[UsersTable.username],
            rating = row[ReviewsTable.rating],
            comment = row[ReviewsTable.comment],
            serviceType = row[ReviewsTable.serviceType],
            serviceDate = row[ReviewsTable.serviceDate],
            pricePaid = row[ReviewsTable.pricePaid],
            priceRating = row[ReviewsTable.priceRating],
            qualityRating = row[ReviewsTable.qualityRating],
            serviceRating = row[ReviewsTable.serviceRating],
            images = try {
                row[ReviewsTable.images]?.let { json.decodeFromString<List<String>>(it) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            createdAt = row[ReviewsTable.createdAt].format(formatter),
            updatedAt = row[ReviewsTable.updatedAt].format(formatter)
        )
}