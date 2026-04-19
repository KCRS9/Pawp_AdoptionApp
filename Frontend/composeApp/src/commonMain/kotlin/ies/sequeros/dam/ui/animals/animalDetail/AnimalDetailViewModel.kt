package ies.sequeros.dam.ui.animals.animalDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetAnimalByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimalDetailViewModel(
    private val getAnimalById: GetAnimalByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnimalDetailState())
    val state: StateFlow<AnimalDetailState> = _state.asStateFlow()

    fun reload(animalId: String) {
        if (animalId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val animal = getAnimalById(animalId)
                _state.update { it.copy(isLoading = false, animal = animal) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun load(animalId: String) {
        if (animalId.isBlank()) return
        if (_state.value.animal?.id == animalId) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val animal = getAnimalById(animalId)
                _state.update { it.copy(isLoading = false, animal = animal) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
