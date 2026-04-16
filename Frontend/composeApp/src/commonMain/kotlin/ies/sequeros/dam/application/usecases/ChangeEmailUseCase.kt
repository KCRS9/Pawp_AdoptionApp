package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.application.comandos.ChangeEmailCommand
import ies.sequeros.dam.domain.repositories.IUserRepository

class ChangeEmailUseCase (private val repository: IUserRepository) {

    suspend operator fun invoke (command: ChangeEmailCommand) =
        repository.changeEmail(command)
}