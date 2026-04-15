package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.application.comandos.ChangeEmailCommand
import ies.sequeros.dam.application.comandos.ChangePasswordCommand
import ies.sequeros.dam.application.comandos.UpdateProfileCommand
import ies.sequeros.dam.domain.models.User

interface IUserRepository {

    suspend fun getCurrentUser(): User

    suspend fun updateProfile(command: UpdateProfileCommand)

    suspend fun updateAvatar(imageBytes: ByteArray, fileName: String)

    suspend fun changePassword(command: ChangePasswordCommand)

    suspend fun changeEmail(command: ChangeEmailCommand)
}