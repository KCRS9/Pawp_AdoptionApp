package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IAnimalRepository

class UpdateAnimalPhotoUseCase(private val repository: IAnimalRepository) {
    suspend operator fun invoke(animalId: String, imageBytes: ByteArray, fileName: String): String =
        repository.uploadPhoto(animalId, imageBytes, fileName)
}
