package ies.sequeros.dam.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.UpdateProfileCommand
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.UpdateAvatarUseCase
import ies.sequeros.dam.application.usecases.UpdateProfileUseCase
import ies.sequeros.dam.ui.appsettings.AppSettings
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class EditProfileViewModel(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val getLocalitiesUseCase: GetLocalitiesUseCase,
    private val settings: AppSettings
): ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()


    init {

        settings.currentUser.value?.let { user ->

            _state.update {
                it.copy(
                    name = user.name,
                    description = user.description ?: "",
                    locationId = user.location,
                    locationName = user.locationName ?: ""
                )
            }
        }

        loadLocalities()
    }


    private fun loadLocalities(){

        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocalities = true) }
            try{
                val list = getLocalitiesUseCase()
                _state.update { it.copy(localities = list, isLoadingLocalities = false) }
            }catch (e: Exception){
                _state.update { it.copy(isLoadingLocalities = false
                ) }
            }
        }
    }

    fun onNameChange(name: String){

        _state.update {
            it.copy(
                name = name,
                nameError = if (name.length >= 2) null else "El nombre debe tener minimo 2 caracteres"
            )
        }
    }

    fun onDescriptionChange( description: String) {

        _state.update { it.copy( description = description) }
    }

    fun onLocationSelect(id: Int, name: String){

        _state.update { it.copy(locationId = id, locationName = name) }
    }

    fun saveProfile(){

        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy( isSaving = true, errorMessage = null) }

            try {

                updateProfileUseCase(
                    UpdateProfileCommand(
                        name = _state.value.name.trim(),
                        locationId = _state.value.locationId,
                        description = _state.value.description.trim().ifBlank { null }
                    )
                )

                // Señalamos éxito — la pantalla llamará refreshCurrentUser() y cerrará

                _state.update { it.copy(isSaving = false, isSaveSuccess = true) }

            }catch (e: Exception){

                _state.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    // Paso 1: el usuario selecciona un archivo → guardamos bytes para previsualizar
    fun onAvatarFileSelected(file: PlatformFile?) {
        if (file == null) {
            _state.update { it.copy(previewBytes = null, previewFileName = null) }
            return
        }
        viewModelScope.launch {
            val bytes = file.readBytes()
            _state.update { it.copy(previewBytes = bytes, previewFileName = file.name ?: "avatar.jpg") }
        }
    }

    // Paso 2: el usuario confirma → se sube la imagen al servidor
    fun confirmAvatar() {
        val bytes    = _state.value.previewBytes    ?: return
        val fileName = _state.value.previewFileName ?: return
        viewModelScope.launch {
            _state.update { it.copy(isUploadingPhoto = true, errorMessage = null) }
            try {
                updateAvatarUseCase(bytes, fileName)
                _state.update { it.copy(
                    isUploadingPhoto = false,
                    isPhotoSuccess   = true,
                    previewBytes     = null,
                    previewFileName  = null
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isUploadingPhoto = false, errorMessage = e.message) }
            }
        }
    }

    /** Resetear flag tras procesar el éxito en la pantalla */
    fun onSaveSuccessHandled()  { _state.update { it.copy(isSaveSuccess = false) } }

    fun onPhotoSuccessHandled() { _state.update { it.copy(isPhotoSuccess = false) } }
}