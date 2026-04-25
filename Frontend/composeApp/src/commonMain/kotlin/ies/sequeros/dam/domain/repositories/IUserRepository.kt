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

    // métodos para el admin
    suspend fun getUserById(userId: String): User

    suspend fun getUsers(skip: Int = 0, limit: Int = 20, search: String? = null): List<User>

    suspend fun updateUserAdmin(userId: String, name: String, email: String, role: String, location: Int, description: String?): Unit

    suspend fun updateUserPhoto(userId: String, imageBytes: ByteArray, fileName: String): String
}