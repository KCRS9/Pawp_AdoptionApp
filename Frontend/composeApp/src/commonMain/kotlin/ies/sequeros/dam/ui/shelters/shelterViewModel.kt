package ies.sequeros.dam.ui.shelters


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetSheltersUseCase
import ies.sequeros.dam.domain.models.ShelterSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProtectorasState(

    val shelters: List<ShelterSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProtectorasViewModel(

    private val getSheltersUseCase: GetSheltersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProtectorasState())
    val state: StateFlow<ProtectorasState> = _state.asStateFlow()

    init {

        loadShelters()
    }

    fun loadShelters() {

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                val shelters = getSheltersUseCase()
                _state.update { it.copy(shelters = shelters, isLoading = false) }

            } catch (e: Exception) {

                println("LOG [ProtectorasViewModel]: Error al cargar protectoras → ${e.message}")
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}