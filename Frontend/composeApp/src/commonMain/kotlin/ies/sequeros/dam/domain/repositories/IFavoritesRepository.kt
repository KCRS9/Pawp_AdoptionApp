package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.AnimalSummary

interface IFavoritesRepository {
    suspend fun getFavorites(): List<AnimalSummary>
    suspend fun getUserFavorites(userId: String): List<AnimalSummary>
    suspend fun addFavorite(animalId: String)
    suspend fun removeFavorite(animalId: String)
}
