package ies.sequeros.dam.ui.adoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetShelterAdoptionsUseCase
import ies.sequeros.dam.domain.models.ShelterAdoption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SolicitudesProtectoraState(
    val adoptions: List<ShelterAdoption> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SolicitudesProtectoraViewModel(
    private val getShelterAdoptions: GetShelterAdoptionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SolicitudesProtectoraState())
    val state: StateFlow<SolicitudesProtectoraState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val adoptions = getShelterAdoptions()
                _state.update { it.copy(adoptions = adoptions, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
