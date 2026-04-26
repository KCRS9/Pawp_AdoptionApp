package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAdoptionRepository

class GetAdoptionDetailUseCase(private val repository: IAdoptionRepository) {
    suspend operator fun invoke(id: Int) = repository.getAdoptionDetail(id)
}
