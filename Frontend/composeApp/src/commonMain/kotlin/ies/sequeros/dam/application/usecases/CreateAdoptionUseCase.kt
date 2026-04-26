package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.AdoptionRequest
import ies.sequeros.dam.domain.repositories.IAdoptionRepository

class CreateAdoptionUseCase(private val repository: IAdoptionRepository) {
    suspend operator fun invoke(request: AdoptionRequest) = repository.createAdoption(request)
}
