package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAdoptionRepository

class GetShelterAdoptionsUseCase(private val repository: IAdoptionRepository) {
    suspend operator fun invoke() = repository.getShelterAdoptions()
}
