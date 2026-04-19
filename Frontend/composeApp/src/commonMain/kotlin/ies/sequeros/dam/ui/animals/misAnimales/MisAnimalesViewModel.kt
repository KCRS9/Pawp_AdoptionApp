package ies.sequeros.dam.ui.animals.misAnimales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetAnimalsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MisAnimalesViewModel(private val getAnimals: GetAnimalsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(MisAnimalesState())
    val state: StateFlow<MisAnimalesState> = _state.asStateFlow()
    private var currentShelterId = ""

    fun load(shelterId: String, status: String = "available") {
        currentShelterId = shelterId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val animals = getAnimals(skip = 0, limit = 100, shelterId = shelterId, status = status)
                _state.update { it.copy(isLoading = false, animals = animals) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun selectStatus(status: String?) {
        val newStatus = status ?: "available"
        _state.update { it.copy(selectedStatus = status) }
        load(currentShelterId, newStatus)
    }
}
