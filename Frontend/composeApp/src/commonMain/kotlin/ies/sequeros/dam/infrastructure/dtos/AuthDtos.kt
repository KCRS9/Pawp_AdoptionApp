package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.logger.MESSAGE

// DTO para POST /users/signup/
@Serializable
data class RegisterRequestDto(

    val name: String,
    val email: String,
    val password: String,
    val location: String,
    val role: String = "user"
)

// Respuesta de POST /users/signup/
@Serializable
data class RegisterResponseDto(

    val id: String,
    val message: String
)

// Respuesta de POST /users/login/
@Serializable
data class LoginResponseDto(

    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type")   val tokenType: String
)

// Respuesta de error del backend
data class ErrorResponseDto(

    val detail: String
)

@Serializable
data class UserProfileDto(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val location: String,
    val role: String,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("shelter_id") val shelterId: String? = null
)