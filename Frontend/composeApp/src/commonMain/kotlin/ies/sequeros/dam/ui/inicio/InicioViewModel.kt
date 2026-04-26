package ies.sequeros.dam.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetAnimalsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class InicioViewModel(private val getAnimals: GetAnimalsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(InicioState())
    val state: StateFlow<InicioState> = _state.asStateFlow()

    init { loadFirst() }

    fun refresh() { loadFirst() }

    fun selectSpecies(species: String?) {
        if (_state.value.selectedSpecies == species) return
        _state.update { it.copy(selectedSpecies = species) }
        loadFirst()
    }

    fun loadMore() {
        val s = _state.value
        if (s.isLoadingMore || !s.hasMore || s.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                val more = getAnimals(
                    skip = s.animals.size,
                    limit = PAGE_SIZE,
                    species = s.selectedSpecies
                ).shuffled()
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        animals = it.animals + more,
                        hasMore = more.size == PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingMore = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadFirst() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, animals = emptyList(), hasMore = true) }
            try {
                val animals = getAnimals(skip = 0, limit = PAGE_SIZE, species = _state.value.selectedSpecies).shuffled()
                _state.update { it.copy(isLoading = false, animals = animals, hasMore = animals.size == PAGE_SIZE) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
