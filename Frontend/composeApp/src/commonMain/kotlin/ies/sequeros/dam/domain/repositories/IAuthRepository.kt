package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.User

interface IAuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String,
        location: Int,
        shelterName: String? = null,
        shelterDescription: String? = null,
        shelterPhone: String? = null,
        shelterEmail: String? = null
    ): User

    suspend fun login(
        email: String,
        password: String
    )
}