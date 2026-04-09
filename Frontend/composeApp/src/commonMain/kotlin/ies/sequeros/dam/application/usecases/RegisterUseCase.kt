package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.application.comandos.RegisterCommand
import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IAuthRepository

class RegisterUseCase(

    private val authRepository: IAuthRepository
) {

    suspend operator fun invoke(command: RegisterCommand): User {

        return authRepository.register(
            name = command.name,
            email = command.email,
            password = command.password,
            location = command.location,
            role = command.role
        )
    }
}