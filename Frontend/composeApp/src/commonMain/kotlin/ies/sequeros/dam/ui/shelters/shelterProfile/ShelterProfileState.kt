package ies.sequeros.dam.ui.shelters.shelterProfile


import ies.sequeros.dam.domain.models.Shelter

data class ShelterProfileState(

    val shelter: Shelter? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)