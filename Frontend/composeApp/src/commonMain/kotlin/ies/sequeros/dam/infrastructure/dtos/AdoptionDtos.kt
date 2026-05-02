package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdoptionRequestDto(
    @SerialName("animal_id") val animalId: String,
    val motivation: String,
    val contact: String,
    @SerialName("housing_type") val housingType: String,
    @SerialName("other_animals") val otherAnimals: Boolean,
    @SerialName("hours_alone") val hoursAlone: Int,
    val experience: String
)

@Serializable
data class MyAdoptionDto(
    val id: Int,
    @SerialName("animal_id") val animalId: String,
    @SerialName("animal_name") val animalName: String,
    @SerialName("animal_image") val animalImage: String? = null,
    @SerialName("shelter_name") val shelterName: String,
    val status: String
)

@Serializable
data class ShelterAdoptionDto(
    val id: Int,
    @SerialName("animal_id") val animalId: String,
    @SerialName("animal_name") val animalName: String,
    @SerialName("animal_image") val animalImage: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_image") val userImage: String? = null,
    val status: String
)

@Serializable
data class AdoptionDetailDto(
    val id: Int,
    @SerialName("animal_id") val animalId: String,
    @SerialName("animal_name") val animalName: String,
    @SerialName("animal_image") val animalImage: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_image") val userImage: String? = null,
    @SerialName("user_location") val userLocation: String? = null,
    @SerialName("shelter_name") val shelterName: String,
    val status: String,
    val motivation: String,
    val contact: String? = null,
    @SerialName("housing_type") val housingType: String? = null,
    @SerialName("other_animals") val otherAnimals: Boolean? = null,
    @SerialName("hours_alone") val hoursAlone: Int? = null,
    val experience: String? = null
)

@Serializable
data class AdoptionStatusDto(val status: String)
