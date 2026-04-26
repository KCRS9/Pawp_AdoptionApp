package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAdoptionRepository

class GetMyAdoptionsUseCase(private val repository: IAdoptionRepository) {
    suspend operator fun invoke() = repository.getMyAdoptions()
}
