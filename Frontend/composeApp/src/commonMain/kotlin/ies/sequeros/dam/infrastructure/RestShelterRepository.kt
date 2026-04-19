package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.Shelter
import ies.sequeros.dam.domain.models.ShelterSummary
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.infrastructure.dtos.ShelterDetailDto
import ies.sequeros.dam.infrastructure.dtos.ShelterLogoResponseDto
import ies.sequeros.dam.infrastructure.dtos.ShelterSummaryDto
import ies.sequeros.dam.infrastructure.dtos.UpdateShelterDto
import ies.sequeros.dam.infrastructure.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

        val shelter = response.body<ShelterDetailDto>().toDomain()
        return shelter.copy(
            profileImage = shelter.profileImage?.let { if (it.startsWith("/")) "$baseUrl$it" else it },
            animals = shelter.animals.map { a ->
                a.copy(profileImage = a.profileImage?.let { if (it.startsWith("/")) "$baseUrl$it" else it })
            }
        )
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

    override suspend fun uploadLogo(shelterId: String, imageBytes: ByteArray, fileName: String): String {

        println("LOG [RestShelterRepository]: Subiendo logo de protectora $shelterId")

        val response = client.post("$baseUrl/shelters/$shelterId/logo") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key   = "file",
                            value = imageBytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                                append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            }
                        )
                    }
                )
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Error al subir el logo (${response.status.value})")
        }

        val rawUrl = response.body<ShelterLogoResponseDto>().profileImage
        return if (rawUrl.startsWith("/")) "$baseUrl$rawUrl" else rawUrl
    }
}