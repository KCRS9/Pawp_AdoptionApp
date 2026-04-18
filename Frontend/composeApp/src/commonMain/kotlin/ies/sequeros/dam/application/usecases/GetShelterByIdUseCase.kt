package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.Shelter
import ies.sequeros.dam.domain.repositories.IShelterRepository

class GetShelterByIdUseCase(private val shelterRepository: IShelterRepository) {

    suspend operator fun invoke(id: String): Shelter {

        return shelterRepository.getShelterById(id)
    }
}