package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.application.comandos.LoginCommand
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.infrastructure.RestAuthRepository

class LoginUseCase (
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(command: LoginCommand){

        authRepository.login(command.email, command.password)
    }
}