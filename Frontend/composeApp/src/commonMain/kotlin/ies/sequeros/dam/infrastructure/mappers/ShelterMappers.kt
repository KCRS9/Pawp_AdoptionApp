package ies.sequeros.dam.infrastructure.mappers

import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.models.Shelter
import ies.sequeros.dam.domain.models.ShelterSummary
import ies.sequeros.dam.infrastructure.dtos.AnimalSummaryDto
import ies.sequeros.dam.infrastructure.dtos.ShelterDetailDto
import ies.sequeros.dam.infrastructure.dtos.ShelterSummaryDto

// Convierte el DTO del listado en la entidad de dominio reducida
fun ShelterSummaryDto.toDomain() = ShelterSummary(
    id = id,
    name = name,
    location = location,
    locationName = locationName,
    animalsAvailable = animalsAvailable,
    profileImage = profileImage
)

// Convierte el DTO de detalle en la entidad completa del dominio
fun ShelterDetailDto.toDomain() = Shelter(
    id = id,
    name = name,
    address = address,
    location = location,
    locationName = locationName,
    phone = phone,
    email = email,
    website = website,
    description = description,
    adminId = adminId,
    adminName = adminName,
    profileImage = profileImage,
    animals = animals.map { it.toDomain() }
)

fun AnimalSummaryDto.toDomain() = AnimalSummary(
    id = id,
    name = name,
    species = species,
    breed = breed,
    gender = gender,
    profileImage = profileImage,
    shelterName = shelterName,
    locationName = locationName
)