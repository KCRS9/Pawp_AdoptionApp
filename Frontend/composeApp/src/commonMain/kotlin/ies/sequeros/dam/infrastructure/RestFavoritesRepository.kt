package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.repositories.IFavoritesRepository
import ies.sequeros.dam.infrastructure.dtos.AnimalSummaryDto
import ies.sequeros.dam.infrastructure.dtos.ErrorResponseDto
import ies.sequeros.dam.infrastructure.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess

class RestFavoritesRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : IFavoritesRepository {

    private fun String?.withBase() =
        if (this != null && startsWith("/")) "$baseUrl$this" else this

    override suspend fun getFavorites(): List<AnimalSummary> {
        val response = client.get("$baseUrl/favorites/")
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al obtener favoritos" }
            throw Exception(detail)
        }
        return response.body<List<AnimalSummaryDto>>().map {
            it.toDomain().copy(profileImage = it.profileImage.withBase())
        }
    }

    override suspend fun getUserFavorites(userId: String): List<AnimalSummary> {
        val response = client.get("$baseUrl/users/$userId/favorites")
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al obtener favoritos del usuario" }
            throw Exception(detail)
        }
        return response.body<List<AnimalSummaryDto>>().map {
            it.toDomain().copy(profileImage = it.profileImage.withBase())
        }
    }

    override suspend fun addFavorite(animalId: String) {
        val response = client.post("$baseUrl/favorites/$animalId")
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al añadir favorito" }
            throw Exception(detail)
        }
    }

    override suspend fun removeFavorite(animalId: String) {
        val response = client.delete("$baseUrl/favorites/$animalId")
        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
                         catch (e: Exception) { "Error al quitar favorito" }
            throw Exception(detail)
        }
    }
}
