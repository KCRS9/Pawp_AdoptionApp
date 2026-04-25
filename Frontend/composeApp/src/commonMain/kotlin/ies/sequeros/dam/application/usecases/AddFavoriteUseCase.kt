package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IFavoritesRepository

class AddFavoriteUseCase(private val repository: IFavoritesRepository) {
    suspend operator fun invoke(animalId: String) = repository.addFavorite(animalId)
}
