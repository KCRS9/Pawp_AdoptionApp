package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.repositories.IFavoritesRepository

class GetUserFavoritesUseCase(private val repository: IFavoritesRepository) {
    suspend operator fun invoke(userId: String): List<AnimalSummary> =
        repository.getUserFavorites(userId)
}
