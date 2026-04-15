package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LocalityDto(

    val id: Int,
    val name: String
)