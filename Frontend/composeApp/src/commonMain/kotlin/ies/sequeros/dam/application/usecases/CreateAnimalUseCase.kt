package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAnimalRepository

class CreateAnimalUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(
        name: String, species: String, breed: String,
        birthDate: String?, gender: String, size: String,
        description: String, status: String, health: String
    ): String = repository.createAnimal(name, species, breed, birthDate, gender, size, description, status, health)
}
