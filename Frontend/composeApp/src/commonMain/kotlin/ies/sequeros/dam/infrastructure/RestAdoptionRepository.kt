package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.AdoptionDetail
import ies.sequeros.dam.domain.models.AdoptionRequest
import ies.sequeros.dam.domain.models.MyAdoption
import ies.sequeros.dam.domain.models.ShelterAdoption
import ies.sequeros.dam.domain.repositories.IAdoptionRepository
import ies.sequeros.dam.infrastructure.dtos.AdoptionDetailDto
import ies.sequeros.dam.infrastructure.dtos.AdoptionRequestDto
import ies.sequeros.dam.infrastructure.dtos.AdoptionStatusDto
import ies.sequeros.dam.infrastructure.dtos.ErrorResponseDto
import ies.sequeros.dam.infrastructure.dtos.MyAdoptionDto
import ies.sequeros.dam.infrastructure.dtos.ShelterAdoptionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class RestAdoptionRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : IAdoptionRepository {

    private fun String?.withBase() =
        if (this != null && startsWith("/")) "$baseUrl$this" else this

    override suspend fun createAdoption(request: AdoptionRequest) {
        val response = client.post("$baseUrl/adoptions/") {
            contentType(ContentType.Application.Json)
            setBody(AdoptionRequestDto(
                animalId = request.animalId,
                motivation = request.motivation,
                contact = request.contact,
                housingType = request.housingType,
                otherAnimals = request.otherAnimals,
                hoursAlone = request.hoursAlone,
                experience = request.experience
            ))
        }
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al enviar la solicitud" }
            throw Exception(detail)
        }
    }

    override suspend fun getMyAdoptions(): List<MyAdoption> {
        val response = client.get("$baseUrl/adoptions/me")
        if (!response.status.isSuccess()) throw Exception("Error al cargar solicitudes")
        return response.body<List<MyAdoptionDto>>().map {
            MyAdoption(
                id = it.id, animalId = it.animalId, animalName = it.animalName,
                animalImage = it.animalImage.withBase(), shelterName = it.shelterName,
                status = it.status
            )
        }
    }

    override suspend fun getShelterAdoptions(): List<ShelterAdoption> {
        val response = client.get("$baseUrl/adoptions/shelter")
        if (!response.status.isSuccess()) throw Exception("Error al cargar solicitudes")
        return response.body<List<ShelterAdoptionDto>>().map {
            ShelterAdoption(
                id = it.id, animalId = it.animalId, animalName = it.animalName,
                animalImage = it.animalImage.withBase(), userId = it.userId,
                userName = it.userName, userImage = it.userImage.withBase(),
                status = it.status
            )
        }
    }

    override suspend fun getAdoptionDetail(id: Int): AdoptionDetail {
        val response = client.get("$baseUrl/adoptions/$id")
        if (!response.status.isSuccess()) throw Exception("Solicitud no encontrada")
        val dto = response.body<AdoptionDetailDto>()
        return AdoptionDetail(
            id = dto.id, animalId = dto.animalId, animalName = dto.animalName,
            animalImage = dto.animalImage.withBase(), userId = dto.userId,
            userName = dto.userName, userImage = dto.userImage.withBase(),
            userLocation = dto.userLocation, shelterName = dto.shelterName, status = dto.status,
            motivation = dto.motivation, contact = dto.contact,
            housingType = dto.housingType, otherAnimals = dto.otherAnimals,
            hoursAlone = dto.hoursAlone, experience = dto.experience
        )
    }

    override suspend fun updateAdoptionStatus(id: Int, status: String) {
        val response = client.patch("$baseUrl/adoptions/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(AdoptionStatusDto(status))
        }
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al actualizar el estado" }
            throw Exception(detail)
        }
    }
}
