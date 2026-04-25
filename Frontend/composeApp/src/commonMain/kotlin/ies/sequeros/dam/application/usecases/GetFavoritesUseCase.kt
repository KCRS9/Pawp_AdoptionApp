package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.repositories.IFavoritesRepository

class GetFavoritesUseCase(private val repository: IFavoritesRepository) {
    suspend operator fun invoke(): List<AnimalSummary> = repository.getFavorites()
}
