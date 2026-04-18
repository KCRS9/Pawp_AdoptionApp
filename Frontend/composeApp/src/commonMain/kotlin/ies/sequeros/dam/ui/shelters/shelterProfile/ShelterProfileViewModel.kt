package ies.sequeros.dam.ui.shelters.shelterProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetShelterByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelterProfileViewModel(

    private val getShelterByIdUseCase: GetShelterByIdUseCase

) : ViewModel() {

    private val _state = MutableStateFlow(ShelterProfileState())
    val state: StateFlow<ShelterProfileState> = _state.asStateFlow()

    fun load(shelterId: String) {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                val shelter = getShelterByIdUseCase(shelterId)
                _state.update { it.copy(shelter = shelter, isLoading = false) }

            } catch (e: Exception) {

                println("LOG [ShelterProfileViewModel]: Error → ${e.message}")
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}