package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAnimalRepository

class UpdateAnimalUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(
        id: String, name: String, species: String, breed: String,
        birthDate: String?, gender: String, size: String,
        description: String, status: String, health: String
    ) = repository.updateAnimal(id, name, species, breed, birthDate, gender, size, description, status, health)
}
