package org.example.services


import io.ktor.server.plugins.*
import org.example.models.AuthResponse
import org.example.models.User
import org.example.models.UserCredentials
import org.example.models.UserResponse
import org.example.repositories.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    suspend fun register(user: User): AuthResponse {
        // Check if username already exists
        val existingUser = userRepository.getUserByUsername(user.username)
        if (existingUser != null) {
            throw BadRequestException("Username already exists")
        }

        // Create the user
        val createdUser = userRepository.createUser(user)
        val token = jwtService.generateToken(createdUser)

        return AuthResponse(token = token, user = createdUser)
    }

    suspend fun login(credentials: UserCredentials): AuthResponse {
        val user = userRepository.validateCredentials(credentials.username, credentials.password)
            ?: throw BadRequestException("Invalid username or password")

        val token = jwtService.generateToken(user)

        return AuthResponse(token = token, user = user)
    }

    suspend fun validateToken(token: String): UserResponse {
        val userId = jwtService.getUserIdFromToken(token)
        return userRepository.getUserById(userId)
            ?: throw BadRequestException("Invalid user token")
    }
}