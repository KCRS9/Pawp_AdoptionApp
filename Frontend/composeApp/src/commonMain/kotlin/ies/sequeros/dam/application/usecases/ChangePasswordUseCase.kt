package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.application.comandos.ChangePasswordCommand
import ies.sequeros.dam.domain.repositories.IUserRepository

class ChangePasswordUseCase(private val repository: IUserRepository) {

    suspend operator fun invoke(command: ChangePasswordCommand) =
        repository.changePassword(command)
}