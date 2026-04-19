package ies.sequeros.dam.ui.animals.misAnimales

import ies.sequeros.dam.domain.models.AnimalSummary

data class MisAnimalesState(
    val animals: List<AnimalSummary> = emptyList(),
    val isLoading: Boolean = false,
    val selectedStatus: String? = null,
    val errorMessage: String? = null
)
