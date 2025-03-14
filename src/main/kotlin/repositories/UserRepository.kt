package org.example.repositories


import org.example.database.DatabaseFactory.dbQuery
import org.example.database.UsersTable
import org.example.models.User
import org.example.models.UserResponse
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class UserRepository {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    suspend fun createUser(user: User): UserResponse = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())

        UsersTable.insert {
            it[UsersTable.id] = id
            it[username] = user.username
            it[email] = user.email
            it[password] = hashedPassword
            it[fullName] = user.fullName
            it[createdAt] = now
            it[updatedAt] = now
        }

        UserResponse(
            id = id,
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            createdAt = now.format(formatter),
            updatedAt = now.format(formatter)
        )
    }

    suspend fun getUserById(id: String): UserResponse? = dbQuery {
        UsersTable.select { UsersTable.id eq id }
            .mapNotNull { toUserResponse(it) }
            .singleOrNull()
    }

    suspend fun getUserByUsername(username: String): User? = dbQuery {
        UsersTable.select { UsersTable.username eq username }
            .mapNotNull { toUser(it) }
            .singleOrNull()
    }

    suspend fun validateCredentials(username: String, password: String): UserResponse? = dbQuery {
        val user = UsersTable.select { UsersTable.username eq username }
            .mapNotNull { toUser(it) }
            .singleOrNull()

        if (user != null && BCrypt.checkpw(password, user.password)) {
            toUserResponse(user)
        } else {
            null
        }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[UsersTable.id],
            username = row[UsersTable.username],
            email = row[UsersTable.email],
            password = row[UsersTable.password],
            fullName = row[UsersTable.fullName],
            createdAt = row[UsersTable.createdAt].format(formatter),
            updatedAt = row[UsersTable.updatedAt].format(formatter)
        )

    private fun toUserResponse(row: ResultRow): UserResponse =
        UserResponse(
            id = row[UsersTable.id],
            username = row[UsersTable.username],
            email = row[UsersTable.email],
            fullName = row[UsersTable.fullName],
            createdAt = row[UsersTable.createdAt].format(formatter),
            updatedAt = row[UsersTable.updatedAt].format(formatter)
        )

    private fun toUserResponse(user: User): UserResponse =
        UserResponse(
            id = user.id ?: throw IllegalArgumentException("User ID cannot be null"),
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            createdAt = user.createdAt ?: LocalDateTime.now().format(formatter),
            updatedAt = user.updatedAt ?: LocalDateTime.now().format(formatter)
        )
}