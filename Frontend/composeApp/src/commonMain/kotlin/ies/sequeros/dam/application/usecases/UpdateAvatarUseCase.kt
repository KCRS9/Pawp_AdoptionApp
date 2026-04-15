package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IUserRepository

class UpdateAvatarUseCase(private val repository: IUserRepository) {

    suspend operator fun invoke(imageBytes: ByteArray, fileName: String) =
        repository.updateAvatar(imageBytes, fileName)
}