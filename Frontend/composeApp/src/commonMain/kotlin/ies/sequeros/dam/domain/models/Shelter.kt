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
    val profileImage: String?
)