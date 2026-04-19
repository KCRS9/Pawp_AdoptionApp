package ies.sequeros.dam.domain.models

data class Shelter(
    val id: String,
    val name: String,
    val address: String?,
    val location: Int,
    val phone: String,
    val email: String,
    val website: String?,
    val description: String,
    val locationName: String? = null,
    val adminId: String,
    val adminName: String?,
    val profileImage: String?,
    val animals: List<AnimalSummary> = emptyList()
)

data class ShelterSummary(
    val id: String,
    val name: String,
    val location: Int,
    val profileImage: String?
)

data class AnimalSummary(
    val id: String,
    val name: String,
    val species: String,
    val breed: String = "",
    val gender: String = "unknown",
    val profileImage: String?,
    val shelterName: String? = null,
    val locationName: String? = null
)