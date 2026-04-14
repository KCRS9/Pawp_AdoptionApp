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
    val location: Int,
    val role: String = "user"
)

// Respuesta(POST): /users/signup/
@Serializable
data class RegisterResponseDto(

    val id: String,
    val message: String
)

// Respuesta(POST): /users/login/
@Serializable
data class LoginResponseDto(

    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type")   val tokenType: String
)

// Respuesta de error del backend
@Serializable
data class ErrorResponseDto(

    val detail: String
)

// Respuesta(GET): /users/me
@Serializable
data class UserProfileDto(

    val id: String,
    val name: String,
    val username: String? = null,
    val email: String,
    val location: Int,
    val role: String,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("shelter_id") val shelterId: String? = null,
    val description: String? = null
)