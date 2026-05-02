package ies.sequeros.dam.ui.shelters.shelterEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.UpdateShelterLogoUseCase
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.utils.ValidationUtils
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelterEditViewModel(
    private val shelterRepository: IShelterRepository,
    private val updateShelterLogo: UpdateShelterLogoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShelterEditState())
    val state: StateFlow<ShelterEditState> = _state.asStateFlow()

    // Carga los datos actuales de la protectora para pre-rellenar el formulario.
    // la guarda del id evita recargar si ya se inicializo con el mismo shelter.
    fun init(shelterId: String) {

        if (_state.value.shelterId == shelterId) return

        viewModelScope.launch {

            _state.update { it.copy(shelterId = shelterId, isLoading = true) }

            try {

                val shelter = shelterRepository.getShelterById(shelterId)

                _state.update {
                    it.copy(
                        isLoading    = false,
                        name         = shelter.name,
                        address      = shelter.address ?: "",
                        phone        = shelter.phone,
                        email        = shelter.email,
                        website      = shelter.website ?: "",
                        description  = shelter.description,
                        profileImage = shelter.profileImage
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

    fun onAddressChange(value: String)     = _state.update { it.copy(address = value) }
    fun onWebsiteChange(value: String)     = _state.update { it.copy(website = value) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }

    // Paso 1: el usuario selecciona un archivo → guardamos bytes para previsualizar
    fun onLogoFileSelected(file: PlatformFile?) {
        if (file == null) {
            _state.update { it.copy(previewBytes = null, previewFileName = null) }
            return
        }
        viewModelScope.launch {
            val bytes = file.readBytes()
            _state.update { it.copy(previewBytes = bytes, previewFileName = file.name) }
        }
    }

    // Paso 2: el usuario confirma → se sube la imagen al servidor
    fun confirmLogo() {
        val bytes    = _state.value.previewBytes    ?: return
        val fileName = _state.value.previewFileName ?: return
        val shelterId = _state.value.shelterId
        viewModelScope.launch {
            _state.update { it.copy(isUploadingPhoto = true) }
            try {
                val newUrl = updateShelterLogo(shelterId, bytes, fileName)
                _state.update { it.copy(
                    isUploadingPhoto = false,
                    isPhotoSuccess   = true,
                    profileImage     = newUrl,
                    previewBytes     = null,
                    previewFileName  = null
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isUploadingPhoto = false, errorMessage = e.message) }
            }
        }
    }

    fun onPhotoSuccessHandled() = _state.update { it.copy(isPhotoSuccess = false) }

    fun save() {

        if (_state.value.isLoading) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                val s = _state.value
                shelterRepository.updateShelter(
                    id          = s.shelterId,
                    name        = s.name,
                    address     = s.address.ifBlank { null },
                    phone       = s.phone,
                    email       = s.email,
                    website     = s.website.ifBlank { null },
                    description = s.description
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {

                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
