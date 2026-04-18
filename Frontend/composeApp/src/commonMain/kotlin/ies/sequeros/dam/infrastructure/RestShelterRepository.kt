package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.Shelter
import ies.sequeros.dam.domain.models.ShelterSummary
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.infrastructure.dtos.ShelterDetailDto
import ies.sequeros.dam.infrastructure.dtos.ShelterSummaryDto
import ies.sequeros.dam.infrastructure.dtos.UpdateShelterDto
import ies.sequeros.dam.infrastructure.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess

class RestShelterRepository(

    private val client: HttpClient,
    private val baseUrl: String
) : IShelterRepository {

    override suspend fun getShelters(): List<ShelterSummary> {

        println("LOG [RestShelterRepository]: Obteniendo listado de protectoras")

        val response = client.get("$baseUrl/shelters/")

        if (!response.status.isSuccess()) {

            throw Exception("Error al obtener protectoras (${response.status.value})")
        }

        return response.body<List<ShelterSummaryDto>>().map { it.toDomain() }
    }

    override suspend fun getShelterById(id: String): Shelter {

        println("LOG [RestShelterRepository]: Obteniendo protectora $id")

        val response = client.get("$baseUrl/shelters/$id")

        if (!response.status.isSuccess()) {

            throw Exception("Protectora no encontrada")
        }

        return response.body<ShelterDetailDto>().toDomain()
    }

    override suspend fun updateShelter(

        id: String,
        name: String,
        address: String?,
        phone: String,
        email: String,
        website: String?,
        description: String
    ) {
        println("LOG [RestShelterRepository]: Actualizando protectora $id")

        val response = client.put("$baseUrl/shelters/$id") {

            setBody(UpdateShelterDto(name, address, phone, email, website, description))
        }

        if (!response.status.isSuccess()) {

            throw Exception("Error al actualizar la protectora (${response.status.value})")
        }
    }
}