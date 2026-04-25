package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IUserRepository

class UpdateUserAdminUseCase(private val repository: IUserRepository) {

    suspend operator fun invoke(

        userId: String, name: String, email: String,
        role: String, location: Int, description: String?
        
    ) = repository.updateUserAdmin(userId, name, email, role, location, description)
}
