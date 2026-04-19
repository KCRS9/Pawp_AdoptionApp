package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IShelterRepository

class UpdateShelterLogoUseCase(private val repository: IShelterRepository) {

    suspend operator fun invoke(shelterId: String, imageBytes: ByteArray, fileName: String): String =
        repository.uploadLogo(shelterId, imageBytes, fileName)
}
