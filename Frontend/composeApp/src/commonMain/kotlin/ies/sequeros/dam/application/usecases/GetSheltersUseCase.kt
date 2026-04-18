package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.ShelterSummary
import ies.sequeros.dam.domain.repositories.IShelterRepository

class GetSheltersUseCase(private val shelterRepository: IShelterRepository) {

    suspend operator fun invoke(): List<ShelterSummary> {

        return shelterRepository.getShelters()
    }
}