package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.Locality
import ies.sequeros.dam.domain.repositories.ILocalityRepository
import ies.sequeros.dam.infrastructure.dtos.LocalityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class RestLocalityRepository (

    private val client: HttpClient,
    private val baseUrl: String

): ILocalityRepository
{

    override suspend fun getLocalities(): List<Locality> {

        println("LOG[RestLocalityRepository]: Solicitando listas de localidades")

        val response = client.get("$baseUrl/localities/")

        if (!response.status.isSuccess())
            throw Exception ("Error al obtener las localidades (${response.status.value})")

        val text = response.bodyAsText()
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(
            ListSerializer(
                LocalityDto.serializer()), text
        ).map { Locality(it.id, it.name) }
    }
}