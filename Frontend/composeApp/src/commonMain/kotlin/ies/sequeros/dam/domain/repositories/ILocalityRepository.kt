package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.Locality

interface ILocalityRepository {

    suspend fun getLocalities(): List<Locality>
}