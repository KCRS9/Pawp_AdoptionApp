package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAnimalRepository

class DeleteAnimalUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(id: String) = repository.deleteAnimal(id)
}
