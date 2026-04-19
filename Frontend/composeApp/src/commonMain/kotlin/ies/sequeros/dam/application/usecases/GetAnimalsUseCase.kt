package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.repositories.IAnimalRepository

class GetAnimalsUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(
        skip: Int = 0,
        limit: Int = 20,
        species: String? = null,
        shelterId: String? = null,
        status: String = "available"
    ): List<AnimalSummary> = repository.getAnimals(skip, limit, species, shelterId, status)
}
