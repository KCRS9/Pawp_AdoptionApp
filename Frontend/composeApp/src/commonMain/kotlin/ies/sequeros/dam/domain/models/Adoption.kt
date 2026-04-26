package ies.sequeros.dam.domain.models

data class AdoptionRequest(
    val animalId: String,
    val motivation: String,
    val contact: String,
    val housingType: String,
    val otherAnimals: Boolean,
    val hoursAlone: Int,
    val experience: String
)

data class MyAdoption(
    val id: Int,
    val animalId: String,
    val animalName: String,
    val animalImage: String?,
    val shelterName: String,
    val status: String
)

data class ShelterAdoption(
    val id: Int,
    val animalId: String,
    val animalName: String,
    val animalImage: String?,
    val userId: String,
    val userName: String,
    val userImage: String?,
    val status: String
)

data class AdoptionDetail(
    val id: Int,
    val animalId: String,
    val animalName: String,
    val animalImage: String?,
    val userId: String,
    val userName: String,
    val userImage: String?,
    val userLocation: String?,
    val shelterName: String,
    val status: String,
    val motivation: String,
    val contact: String?,
    val housingType: String?,
    val otherAnimals: Boolean?,
    val hoursAlone: Int?,
    val experience: String?
)
