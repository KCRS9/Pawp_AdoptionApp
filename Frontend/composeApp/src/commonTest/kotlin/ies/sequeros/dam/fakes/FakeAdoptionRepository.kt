package ies.sequeros.dam.fakes

import ies.sequeros.dam.domain.models.AdoptionDetail
import ies.sequeros.dam.domain.models.AdoptionRequest
import ies.sequeros.dam.domain.models.MyAdoption
import ies.sequeros.dam.domain.models.ShelterAdoption
import ies.sequeros.dam.domain.repositories.IAdoptionRepository

// Implementación falsa de IAdoptionRepository para usar en tests.
// shouldFail controla si queremos simular un error de red en los métodos que probamos.
class FakeAdoptionRepository(private val shouldFail: Boolean = false) : IAdoptionRepository {

    override suspend fun getMyAdoptions(): List<MyAdoption> {
        if (shouldFail) throw Exception("Error de red")
        return listOf(
            MyAdoption(
                id = 1,
                animalId = "animal-001",
                animalName = "Rex",
                animalImage = null,
                shelterName = "Protectora Test",
                status = "pending"
            )
        )
    }

    override suspend fun getShelterAdoptions(): List<ShelterAdoption> {
        if (shouldFail) throw Exception("Error de red")
        return listOf(
            ShelterAdoption(
                id = 1,
                animalId = "animal-001",
                animalName = "Rex",
                animalImage = null,
                userId = "user-001",
                userName = "Test User",
                userImage = null,
                status = "pending"
            )
        )
    }

    // Métodos que no usamos en los tests actuales
    override suspend fun createAdoption(request: AdoptionRequest) = Unit
    override suspend fun getAdoptionDetail(id: Int): AdoptionDetail = throw NotImplementedError()
    override suspend fun updateAdoptionStatus(id: Int, status: String) = Unit
}
