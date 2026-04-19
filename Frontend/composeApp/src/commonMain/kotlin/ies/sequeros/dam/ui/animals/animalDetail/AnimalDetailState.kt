package ies.sequeros.dam.ui.animals.animalDetail

import ies.sequeros.dam.domain.models.Animal

data class AnimalDetailState(
    val animal: Animal? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
