package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.Shelter
import ies.sequeros.dam.domain.models.ShelterSummary

interface IShelterRepository {

    suspend fun getShelters(): List<ShelterSummary>

    suspend fun getShelterById(id: String): Shelter

    suspend fun updateShelter(
        id: String,
        name: String,
        address: String?,
        phone: String,
        email: String,
        website: String?,
        description: String
    )
}