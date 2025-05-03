package org.example.repositories

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.database.DatabaseFactory.dbQuery
import org.example.database.MechanicsTable
import org.example.database.ReviewsTable
import org.example.models.Mechanic
import org.example.models.MechanicWithRating
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MechanicRepository {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createMechanic(mechanic: Mechanic): Mechanic = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        MechanicsTable.insert {
            it[MechanicsTable.id] = id
            it[name] = mechanic.name
            it[address] = mechanic.address
            it[city] = mechanic.city
            it[state] = mechanic.state
            it[zipCode] = mechanic.zipCode
            it[phone] = mechanic.phone
            it[email] = mechanic.email
            it[website] = mechanic.website
            it[specialties] = json.encodeToString(mechanic.specialties)
            it[operatingHours] = mechanic.operatingHours
            it[createdAt] = now
            it[updatedAt] = now
        }

        mechanic.copy(
            id = id,
            createdAt = now.format(formatter),
            updatedAt = now.format(formatter)
        )
    }

    suspend fun getMechanicById(id: String): Mechanic? = dbQuery {
        MechanicsTable.select { MechanicsTable.id eq id }
            .mapNotNull { toMechanic(it) }
            .singleOrNull()
    }

    suspend fun getMechanicWithRating(id: String): MechanicWithRating? = dbQuery {
        val mechanic = MechanicsTable.select { MechanicsTable.id eq id }
            .mapNotNull { toMechanic(it) }
            .singleOrNull() ?: return@dbQuery null

        val ratingStats = ReviewsTable
            .slice(
                ReviewsTable.rating.avg().alias("averageRating"),
                ReviewsTable.id.count().alias("totalReviews")
            )
            .select { ReviewsTable.mechanicId eq id }
            .first()

        val averageRating = ratingStats[ReviewsTable.rating.avg()].toString().toDoubleOrNull() ?: 0.0
        val totalReviews = ratingStats[ReviewsTable.id.count()].toString().toIntOrNull() ?: 0

        MechanicWithRating(
            id = mechanic.id ?: "",
            name = mechanic.name,
            address = mechanic.address,
            city = mechanic.city,
            state = mechanic.state,
            zipCode = mechanic.zipCode,
            phone = mechanic.phone,
            email = mechanic.email,
            website = mechanic.website,
            specialties = mechanic.specialties,
            operatingHours = mechanic.operatingHours,
            averageRating = averageRating,
            totalReviews = totalReviews,
            createdAt = mechanic.createdAt ?: "",
            updatedAt = mechanic.updatedAt ?: ""
        )
    }

    suspend fun searchMechanics(
        query: String? = null,
        city: String? = null,
        state: String? = null,
        specialty: String? = null
    ): List<MechanicWithRating> = dbQuery {
        // Build the base query
        var queryBuilder = MechanicsTable.selectAll()

        // Apply filters
        if (!query.isNullOrBlank()) {
            queryBuilder = queryBuilder.andWhere {
                (MechanicsTable.name.lowerCase() like "%${query.lowercase()}%") or (MechanicsTable.address.lowerCase() like "%${query.lowercase()}%")
            }
        }

        if (!city.isNullOrBlank()) {
            queryBuilder = queryBuilder.andWhere { MechanicsTable.city.lowerCase() like "%${city.lowercase()}%" }
        }

        if (!state.isNullOrBlank()) {
            queryBuilder = queryBuilder.andWhere { MechanicsTable.state.lowerCase() like "%${state.lowercase()}%" }
        }

        // Get all mechanics that match the criteria
        val mechanics = queryBuilder.mapNotNull { toMechanic(it) }

        // If specialty filter is applied, filter further in memory (since specialties is stored as JSON)
        val filteredMechanics = if (!specialty.isNullOrBlank()) {
            mechanics.filter { mechanic ->
                mechanic.specialties.any { it.contains(specialty, ignoreCase = true) }
            }
        } else {
            mechanics
        }

        // For each mechanic, calculate their rating
        filteredMechanics.map { mechanic ->
            val ratingStats = ReviewsTable
                .slice(
                    ReviewsTable.rating.avg().alias("averageRating"),
                    ReviewsTable.id.count().alias("totalReviews")
                )
                .select { ReviewsTable.mechanicId eq (mechanic.id ?: "") }
                .first()

            val averageRating = ratingStats[ReviewsTable.rating.avg()].toString().toDoubleOrNull() ?: 0.0
            val totalReviews = ratingStats[ReviewsTable.id.count()].toString().toIntOrNull() ?: 0

            MechanicWithRating(
                id = mechanic.id ?: "",
                name = mechanic.name,
                address = mechanic.address,
                city = mechanic.city,
                state = mechanic.state,
                zipCode = mechanic.zipCode,
                phone = mechanic.phone,
                email = mechanic.email,
                website = mechanic.website,
                specialties = mechanic.specialties,
                operatingHours = mechanic.operatingHours,
                averageRating = averageRating,
                totalReviews = totalReviews,
                createdAt = mechanic.createdAt ?: "",
                updatedAt = mechanic.updatedAt ?: ""
            )
        }
    }

    private fun toMechanic(row: ResultRow): Mechanic =
        Mechanic(
            id = row[MechanicsTable.id],
            name = row[MechanicsTable.name],
            address = row[MechanicsTable.address],
            city = row[MechanicsTable.city],
            state = row[MechanicsTable.state],
            zipCode = row[MechanicsTable.zipCode],
            phone = row[MechanicsTable.phone],
            email = row[MechanicsTable.email],
            website = row[MechanicsTable.website],
            specialties = try {
                json.decodeFromString<List<String>>(row[MechanicsTable.specialties])
            } catch (e: Exception) {
                emptyList()
            },
            operatingHours = row[MechanicsTable.operatingHours],
            createdAt = row[MechanicsTable.createdAt].format(formatter),
            updatedAt = row[MechanicsTable.updatedAt].format(formatter)
        )
}