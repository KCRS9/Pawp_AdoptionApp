package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.User

interface IAuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String,
        location: String,
        role: String
    ): User

    suspend fun login(
        email: String,
        password: String
    )
}