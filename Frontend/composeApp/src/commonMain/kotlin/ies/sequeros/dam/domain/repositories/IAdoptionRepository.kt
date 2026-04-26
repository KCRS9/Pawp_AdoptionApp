package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.AdoptionDetail
import ies.sequeros.dam.domain.models.AdoptionRequest
import ies.sequeros.dam.domain.models.MyAdoption
import ies.sequeros.dam.domain.models.ShelterAdoption

interface IAdoptionRepository {
    suspend fun createAdoption(request: AdoptionRequest)
    suspend fun getMyAdoptions(): List<MyAdoption>
    suspend fun getShelterAdoptions(): List<ShelterAdoption>
    suspend fun getAdoptionDetail(id: Int): AdoptionDetail
    suspend fun updateAdoptionStatus(id: Int, status: String)
}
