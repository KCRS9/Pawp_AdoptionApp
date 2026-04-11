package ies.sequeros.dam.infrastructure.mappers

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.infrastructure.dtos.RegisterResponseDto


fun RegisterResponseDto.toDomain(

    name: String,
    email: String,
    location: String,
    role: String
): User{

    return User(
        id = this.id,
        name = name,
        email = email,
        location = location,
        role = role,
        profileImage = null,
        shelterId = null
    )
}