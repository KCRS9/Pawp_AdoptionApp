package ies.sequeros.dam.ui.shelters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.GetSheltersUseCase
import ies.sequeros.dam.domain.models.Locality
import ies.sequeros.dam.domain.models.ShelterSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProtectorasState(

    val shelters: List<ShelterSummary> = emptyList(),
    val localities: List<Locality> = emptyList(),
    val selectedLocation: Locality? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProtectorasViewModel(

    private val getSheltersUseCase: GetSheltersUseCase,
    private val getLocalitiesUseCase: GetLocalitiesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProtectorasState())
    val state: StateFlow<ProtectorasState> = _state.asStateFlow()

    init {
        loadLocalities()
        loadShelters()
    }

    fun loadShelters() {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {

                val shelters = getSheltersUseCase(location = _state.value.selectedLocation?.id)
                _state.update { it.copy(shelters = shelters, isLoading = false) }

            } catch (e: Exception) {
                println("LOG [ProtectorasViewModel]: Error → ${e.message}")
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun selectLocation(locality: Locality?) {

        if (_state.value.selectedLocation == locality) return
        _state.update { it.copy(selectedLocation = locality) }
        loadShelters()
    }

    private fun loadLocalities() {

        viewModelScope.launch {

            try {

                val localities = getLocalitiesUseCase()
                _state.update { it.copy(localities = localities) }

            } catch (e: Exception) {

                println("LOG [ProtectorasViewModel]: Error cargando localidades → ${e.message}")
            }
        }
    }
}