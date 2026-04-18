package ies.sequeros.dam.ui.shelters.shelterEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelterEditViewModel(

    private val shelterRepository: IShelterRepository

) : ViewModel() {

    private val _state = MutableStateFlow(ShelterEditState())
    val state: StateFlow<ShelterEditState> = _state.asStateFlow()

    // Carga los datos actuales de la protectora para pre-rellenar el formulario.
    // La guarda del id evita recargar si ya se inicializó con el mismo shelter.
    fun init(shelterId: String) {

        if (_state.value.shelterId == shelterId) return

        viewModelScope.launch {

            _state.update { it.copy(shelterId = shelterId, isLoading = true) }

            try {

                val shelter = shelterRepository.getShelterById(shelterId)

                _state.update {
                    it.copy(
                        isLoading = false,
                        name = shelter.name,
                        address = shelter.address ?: "",
                        phone = shelter.phone,
                        email = shelter.email,
                        website = shelter.website ?: "",
                        description = shelter.description
                    )
                }
            } catch (e: Exception) {

                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onNameChange(value: String) {

        _state.update {
            it.copy(
                name = value,
                nameError = if (value.isNotBlank()) null else "El nombre es obligatorio"
            )
        }
    }

    fun onPhoneChange(value: String) {

        _state.update {
            it.copy(
                phone = value,
                phoneError = if (value.length >= 9) null else "Teléfono no válido"
            )
        }
    }

    fun onEmailChange(value: String) {

        _state.update {
            it.copy(
                email = value,
                emailError = ValidationUtils.emailError(value)
            )
        }
    }

    fun onAddressChange(value: String) {

        _state.update { it.copy(address = value) }
    }

    fun onWebsiteChange(value: String) {

        _state.update { it.copy(website = value) }
    }

    fun onDescriptionChange(value: String) {

        _state.update { it.copy(description = value) }
    }

    fun save() {

        if (_state.value.isLoading) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                val s = _state.value
                shelterRepository.updateShelter(
                    id = s.shelterId,
                    name = s.name,
                    address = s.address.ifBlank { null },
                    phone = s.phone,
                    email = s.email,
                    website = s.website.ifBlank { null },
                    description = s.description
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
