package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.User

interface IUserRepository {

    suspend fun getCurrentUser(): User
}