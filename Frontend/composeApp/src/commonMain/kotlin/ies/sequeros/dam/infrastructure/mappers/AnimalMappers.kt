package ies.sequeros.dam.infrastructure.mappers

import ies.sequeros.dam.domain.models.Animal
import ies.sequeros.dam.infrastructure.dtos.AnimalDetailDto

fun AnimalDetailDto.toDomain() = Animal(
    id = id,
    name = name,
    species = species,
    breed = breed,
    birthDate = birthDate,
    gender = gender,
    size = size,
    description = description,
    health = health,
    status = status,
    shelterId = shelterId,
    shelterName = shelterName,
    locationName = locationName,
    profileImage = profileImage
)
