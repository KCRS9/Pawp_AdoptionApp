package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IUserRepository

class GetCurrentUserUseCase(
    private val repository: IUserRepository
) {

    suspend operator fun invoke(): User = repository.getCurrentUser()
}