package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.Animal
import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.repositories.IAnimalRepository
import ies.sequeros.dam.infrastructure.dtos.AnimalDetailDto
import ies.sequeros.dam.infrastructure.dtos.AnimalPhotoResponseDto
import ies.sequeros.dam.infrastructure.dtos.AnimalSummaryDto
import ies.sequeros.dam.infrastructure.dtos.CreateAnimalDto
import ies.sequeros.dam.infrastructure.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
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

class RestAnimalRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : IAnimalRepository {

    private fun String?.withBase() =
        if (this != null && startsWith("/")) "$baseUrl$this" else this

    override suspend fun getAnimals(
        skip: Int,
        limit: Int,
        species: String?,
        shelterId: String?,
        status: String
    ): List<AnimalSummary> {
        println("LOG [RestAnimalRepository]: getAnimals skip=$skip species=$species shelter=$shelterId status=$status")
        var url = "$baseUrl/animals/?skip=$skip&limit=$limit&status=$status"
        if (species != null) url += "&species=$species"
        if (shelterId != null) url += "&shelter_id=$shelterId"
        val response = client.get(url)
        if (!response.status.isSuccess())
            throw Exception("Error al obtener animales (${response.status.value})")
        return response.body<List<AnimalSummaryDto>>().map {
            it.toDomain().copy(profileImage = it.profileImage.withBase())
        }
    }

    override suspend fun getAnimalById(id: String): Animal {
        println("LOG [RestAnimalRepository]: getAnimalById $id")
        val response = client.get("$baseUrl/animals/$id")
        if (!response.status.isSuccess()) throw Exception("Animal no encontrado")
        val animal = response.body<AnimalDetailDto>().toDomain()
        return animal.copy(profileImage = animal.profileImage.withBase())
    }

    override suspend fun createAnimal(
        name: String, species: String, breed: String,
        birthDate: String?, gender: String, size: String,
        description: String, status: String, health: String
    ): String {
        println("LOG [RestAnimalRepository]: createAnimal $name")
        val response = client.post("$baseUrl/animals/") {
            setBody(CreateAnimalDto(name, species, breed, birthDate, gender, size, description, status, health))
        }
        if (!response.status.isSuccess())
            throw Exception("Error al crear el animal (${response.status.value})")
        return response.body<Map<String, String>>()["id"] ?: ""
    }

    override suspend fun updateAnimal(
        id: String, name: String, species: String, breed: String,
        birthDate: String?, gender: String, size: String,
        description: String, status: String, health: String
    ) {
        println("LOG [RestAnimalRepository]: updateAnimal $id")
        val response = client.put("$baseUrl/animals/$id") {
            setBody(CreateAnimalDto(name, species, breed, birthDate, gender, size, description, status, health))
        }
        if (!response.status.isSuccess())
            throw Exception("Error al actualizar el animal (${response.status.value})")
    }

    override suspend fun deleteAnimal(id: String) {
        println("LOG [RestAnimalRepository]: deleteAnimal $id")
        val response = client.delete("$baseUrl/animals/$id")
        if (!response.status.isSuccess())
            throw Exception("Error al eliminar el animal (${response.status.value})")
    }

    override suspend fun uploadPhoto(
        animalId: String, imageBytes: ByteArray, fileName: String
    ): String {
        println("LOG [RestAnimalRepository]: uploadPhoto animalId=$animalId")
        val ext = fileName.substringAfterLast(".").lowercase()
        val contentType = when (ext) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png" -> ContentType.Image.PNG
            "webp" -> ContentType("image", "webp")
            else -> ContentType.Image.JPEG
        }
        val response = client.post("$baseUrl/animals/$animalId/photo") {
            setBody(MultiPartFormDataContent(formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, contentType.toString())
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }))
        }
        if (!response.status.isSuccess())
            throw Exception("Error al subir la foto (${response.status.value})")
        val url = response.body<AnimalPhotoResponseDto>().profileImage
        return if (url.startsWith("/")) "$baseUrl$url" else url
    }
}
