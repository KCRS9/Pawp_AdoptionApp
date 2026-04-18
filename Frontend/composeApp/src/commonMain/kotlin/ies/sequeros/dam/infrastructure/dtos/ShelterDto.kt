package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO para el listado GET /shelters/
// Solo los campos mínimos para la ficha simplificada
@Serializable
data class ShelterSummaryDto(
    val id: String,
    val name: String,
    val location: Int,
    @SerialName("profile_image") val profileImage: String? = null
)

// DTO para el detalle GET /shelters/{id}
@Serializable
data class ShelterDetailDto(
    val id: String,
    val name: String,
    val address: String? = null,
    val location: Int,
    val phone: String,
    val email: String,
    val website: String? = null,
    val description: String,
    @SerialName("admin_id")   val adminId: String,
    @SerialName("admin_name") val adminName: String? = null,
    @SerialName("profile_image") val profileImage: String? = null,
    val animals: List<AnimalSummaryDto> = emptyList()
)

@Serializable
data class AnimalSummaryDto(
    val id: String,
    val name: String,
    val species: String,
    @SerialName("profile_image") val profileImage: String? = null
)

// DTO para PUT /shelters/{id}
@Serializable
data class UpdateShelterDto(
    val name: String,
    val address: String?,
    val phone: String,
    val email: String,
    val website: String?,
    val description: String
)