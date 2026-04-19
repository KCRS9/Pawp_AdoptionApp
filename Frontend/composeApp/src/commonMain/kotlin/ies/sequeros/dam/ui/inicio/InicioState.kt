package ies.sequeros.dam.ui.inicio

import ies.sequeros.dam.domain.models.AnimalSummary

data class InicioState(
    val animals: List<AnimalSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val selectedSpecies: String? = null,
    val errorMessage: String? = null
)
