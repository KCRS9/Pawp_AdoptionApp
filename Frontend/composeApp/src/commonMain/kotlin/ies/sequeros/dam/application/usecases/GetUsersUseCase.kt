package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IUserRepository

class GetUsersUseCase(private val repository: IUserRepository) {
    
    suspend operator fun invoke(skip: Int = 0, limit: Int = 20, search: String? = null): List<User> =
        repository.getUsers(skip, limit, search)
}
