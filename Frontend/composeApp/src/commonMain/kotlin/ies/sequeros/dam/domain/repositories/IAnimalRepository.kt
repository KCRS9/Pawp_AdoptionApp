package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.Animal
import ies.sequeros.dam.domain.models.AnimalSummary

interface IAnimalRepository {

    suspend fun getAnimals(
        skip: Int = 0,
        limit: Int = 20,
        species: String? = null,
        shelterId: String? = null,
        status: String = "available"
    ): List<AnimalSummary>

    suspend fun getAnimalById(id: String): Animal

    suspend fun createAnimal(
        name: String,
        species: String,
        breed: String,
        birthDate: String?,
        gender: String,
        size: String,
        description: String,
        status: String,
        health: String
    ): String

    suspend fun updateAnimal(
        id: String,
        name: String,
        species: String,
        breed: String,
        birthDate: String?,
        gender: String,
        size: String,
        description: String,
        status: String,
        health: String
    )

    suspend fun deleteAnimal(id: String)

    suspend fun uploadPhoto(
        animalId: String,
        imageBytes: ByteArray,
        fileName: String
    ): String
}
