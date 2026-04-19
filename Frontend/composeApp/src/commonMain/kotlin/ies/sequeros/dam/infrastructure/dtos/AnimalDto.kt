package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimalDetailDto(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    @SerialName("birth_date") val birthDate: String? = null,
    val gender: String = "unknown",
    val size: String,
    val description: String,
    val health: String,
    val status: String,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("shelter_id") val shelterId: String,
    @SerialName("shelter_name") val shelterName: String,
    @SerialName("location_name") val locationName: String? = null
)

@Serializable
data class CreateAnimalDto(
    val name: String,
    val species: String,
    val breed: String,
    @SerialName("birth_date") val birthDate: String? = null,
    val gender: String = "unknown",
    val size: String,
    val description: String,
    val status: String,
    val health: String
)

@Serializable
data class AnimalPhotoResponseDto(
    @SerialName("profile_image") val profileImage: String
)
