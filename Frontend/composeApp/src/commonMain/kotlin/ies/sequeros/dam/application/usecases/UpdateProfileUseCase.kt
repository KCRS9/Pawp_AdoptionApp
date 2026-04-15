package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.application.comandos.UpdateProfileCommand
import ies.sequeros.dam.domain.repositories.IUserRepository

class UpdateProfileUseCase(private val repository: IUserRepository) {

    suspend operator fun invoke(command: UpdateProfileCommand) =
        repository.updateProfile(command)
}