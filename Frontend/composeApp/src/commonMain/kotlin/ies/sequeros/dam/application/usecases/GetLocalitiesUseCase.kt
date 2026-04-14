package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.Locality
import ies.sequeros.dam.domain.repositories.ILocalityRepository

class GetLocalitiesUseCase(
    private val repository: ILocalityRepository

) {

    suspend operator fun invoke(): List<Locality> = repository.getLocalities()
}