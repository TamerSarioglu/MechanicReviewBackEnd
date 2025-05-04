package org.example.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserDto(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String? = null
)

@Serializable
data class LoginDto(
    val username: String,
    val password: String
)

// Validation errors
sealed class ValidationError(val message: String) {
    class InvalidEmail(email: String) : ValidationError("Invalid email format: $email")
    class WeakPassword : ValidationError("Password must be at least 8 characters long and contain uppercase, lowercase, numbers, and special characters")
    class UsernameRequired : ValidationError("Username is required")
    class EmailRequired : ValidationError("Email is required")
    class PasswordRequired : ValidationError("Password is required")
}

// Validation functions
object AuthValidation {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    private val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    
    fun validateEmail(email: String?): ValidationError? {
        if (email.isNullOrBlank()) return ValidationError.EmailRequired()
        if (!EMAIL_REGEX.matches(email)) return ValidationError.InvalidEmail(email)
        return null
    }
    
    fun validatePassword(password: String?): ValidationError? {
        if (password.isNullOrBlank()) return ValidationError.PasswordRequired()
        if (!PASSWORD_REGEX.matches(password)) return ValidationError.WeakPassword()
        return null
    }
    
    fun validateUsername(username: String?): ValidationError? {
        if (username.isNullOrBlank()) return ValidationError.UsernameRequired()
        return null
    }
    
    fun validateRegisterUser(dto: RegisterUserDto): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        validateUsername(dto.username)?.let { errors.add(it) }
        validateEmail(dto.email)?.let { errors.add(it) }
        validatePassword(dto.password)?.let { errors.add(it) }
        
        return errors
    }
    
    fun validateLogin(dto: LoginDto): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        validateUsername(dto.username)?.let { errors.add(it) }
        validatePassword(dto.password)?.let { errors.add(it) }
        
        return errors
    }
} 