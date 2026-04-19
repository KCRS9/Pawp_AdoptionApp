package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.Animal
import ies.sequeros.dam.domain.repositories.IAnimalRepository

class GetAnimalByIdUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(id: String): Animal = repository.getAnimalById(id)
}
