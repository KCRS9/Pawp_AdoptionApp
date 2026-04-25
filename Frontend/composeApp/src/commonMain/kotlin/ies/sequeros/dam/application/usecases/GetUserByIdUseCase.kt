package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IUserRepository

class GetUserByIdUseCase(private val repository: IUserRepository) {
    
    suspend operator fun invoke(userId: String): User = repository.getUserById(userId)
}
