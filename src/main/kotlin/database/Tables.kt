package org.example.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 255)
    val fullName = varchar("full_name", 100).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object MechanicsTable : Table("mechanics") {
    val id = varchar("id", 36)
    val name = varchar("name", 100)
    val address = varchar("address", 255)
    val city = varchar("city", 50)
    val state = varchar("state", 50)
    val zipCode = varchar("zip_code", 20)
    val phone = varchar("phone", 20)
    val email = varchar("email", 100).nullable()
    val website = varchar("website", 255).nullable()
    val specialties = text("specialties") // Stored as JSON string
    val operatingHours = text("operating_hours").nullable() // Stored as JSON string
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object ReviewsTable : Table("reviews") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val mechanicId = varchar("mechanic_id", 36).references(MechanicsTable.id)
    val rating = integer("rating") // 1-5 stars
    val comment = text("comment")
    val serviceType = varchar("service_type", 100).nullable()
    val serviceDate = varchar("service_date", 50).nullable() // stored as ISO string
    val pricePaid = double("price_paid").nullable()
    val priceRating = integer("price_rating").nullable() // 1-5 stars
    val qualityRating = integer("quality_rating").nullable() // 1-5 stars
    val serviceRating = integer("service_rating").nullable() // 1-5 stars
    val images = text("images").nullable() // Stored as JSON array of URLs
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}