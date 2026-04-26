package ies.sequeros.dam.ui.adoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetAdoptionDetailUseCase
import ies.sequeros.dam.application.usecases.UpdateAdoptionStatusUseCase
import ies.sequeros.dam.domain.models.AdoptionDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdoptionDetailState(
    val adoption: AdoptionDetail? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null
)

class AdoptionDetailViewModel(
    private val getAdoptionDetail: GetAdoptionDetailUseCase,
    private val updateAdoptionStatus: UpdateAdoptionStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdoptionDetailState())
    val state: StateFlow<AdoptionDetailState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val detail = getAdoptionDetail(id)
                _state.update { it.copy(adoption = detail, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateStatus(id: Int, status: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }
            try {
                updateAdoptionStatus(id, status)
                val updated = getAdoptionDetail(id)
                _state.update { it.copy(adoption = updated, isUpdating = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isUpdating = false, errorMessage = e.message) }
            }
        }
    }
}
