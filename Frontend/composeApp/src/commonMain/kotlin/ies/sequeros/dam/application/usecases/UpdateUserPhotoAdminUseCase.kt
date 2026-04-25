package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IUserRepository

class UpdateUserPhotoAdminUseCase(private val repository: IUserRepository) {
    
    suspend operator fun invoke(userId: String, imageBytes: ByteArray, fileName: String): String =
        repository.updateUserPhoto(userId, imageBytes, fileName)
}
