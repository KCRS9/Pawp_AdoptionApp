package ies.sequeros.dam.ui.adoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.CreateAdoptionUseCase
import ies.sequeros.dam.domain.models.AdoptionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdoptionFormState(
    val phone: String = "",
    val shareEmail: Boolean = false,
    val housingType: String = "",
    val otherAnimals: Boolean = false,
    val hoursAlone: String = "",
    val experience: String = "",
    val motivation: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = phone.isNotBlank() &&
                housingType.isNotBlank() &&
                hoursAlone.isNotBlank() &&
                experience.isNotBlank() &&
                motivation.isNotBlank()
}

class AdoptionFormViewModel(
    private val createAdoption: CreateAdoptionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdoptionFormState())
    val state: StateFlow<AdoptionFormState> = _state.asStateFlow()

    fun onPhoneChange(value: String)         = _state.update { it.copy(phone = value.filter(Char::isDigit)) }
    fun onShareEmailChange(value: Boolean)   = _state.update { it.copy(shareEmail = value) }
    fun onHousingTypeChange(value: String)   = _state.update { it.copy(housingType = value) }
    fun onOtherAnimalsChange(value: Boolean) = _state.update { it.copy(otherAnimals = value) }
    fun onHoursAloneChange(value: String)    = _state.update { it.copy(hoursAlone = value.filter(Char::isDigit)) }
    fun onExperienceChange(value: String)    = _state.update { it.copy(experience = value) }
    fun onMotivationChange(value: String)    = _state.update { it.copy(motivation = value) }

    fun onSuccessHandled() = _state.update { it.copy(isSuccess = false) }

    fun reset() = _state.update { AdoptionFormState() }

    fun submit(animalId: String, userEmail: String) {
        val s = _state.value
        val contact = if (s.shareEmail) "${s.phone}|$userEmail" else s.phone
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                createAdoption(AdoptionRequest(
                    animalId = animalId,
                    motivation = s.motivation,
                    contact = contact,
                    housingType = s.housingType,
                    otherAnimals = s.otherAnimals,
                    hoursAlone = s.hoursAlone.toIntOrNull() ?: 0,
                    experience = s.experience
                ))
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
