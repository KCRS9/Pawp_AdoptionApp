package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAdoptionRepository

class UpdateAdoptionStatusUseCase(private val repository: IAdoptionRepository) {
    suspend operator fun invoke(id: Int, status: String) = repository.updateAdoptionStatus(id, status)
}
