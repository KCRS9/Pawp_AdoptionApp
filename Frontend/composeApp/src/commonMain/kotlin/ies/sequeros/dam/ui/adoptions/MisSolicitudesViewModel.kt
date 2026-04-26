package ies.sequeros.dam.ui.adoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetMyAdoptionsUseCase
import ies.sequeros.dam.domain.models.MyAdoption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisSolicitudesState(
    val adoptions: List<MyAdoption> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MisSolicitudesViewModel(
    private val getMyAdoptions: GetMyAdoptionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MisSolicitudesState())
    val state: StateFlow<MisSolicitudesState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val adoptions = getMyAdoptions()
                _state.update { it.copy(adoptions = adoptions, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
