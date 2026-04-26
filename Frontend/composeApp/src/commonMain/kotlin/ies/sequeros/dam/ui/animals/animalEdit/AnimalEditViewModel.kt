package ies.sequeros.dam.ui.animals.animalEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.CreateAnimalUseCase
import ies.sequeros.dam.application.usecases.DeleteAnimalUseCase
import ies.sequeros.dam.application.usecases.GetAnimalByIdUseCase
import ies.sequeros.dam.application.usecases.UpdateAnimalPhotoUseCase
import ies.sequeros.dam.application.usecases.UpdateAnimalUseCase
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimalEditViewModel(
    private val getAnimalById: GetAnimalByIdUseCase,
    private val createAnimal: CreateAnimalUseCase,
    private val updateAnimal: UpdateAnimalUseCase,
    private val deleteAnimal: DeleteAnimalUseCase,
    private val updateAnimalPhoto: UpdateAnimalPhotoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnimalEditState())
    val state: StateFlow<AnimalEditState> = _state.asStateFlow()

    fun initCreate() {
        _state.value = AnimalEditState()
    }

    fun initEdit(animalId: String) {
        if (_state.value.animalId == animalId && !_state.value.isCreateMode) return
        viewModelScope.launch {
            _state.update { it.copy(animalId = animalId, isLoading = true, createdAnimalId = null, isUpdated = false, isDeleted = false) }
            try {
                val a = getAnimalById(animalId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = a.name,
                        species = a.species,
                        breed = a.breed,
                        birthDate = a.birthDate ?: "",
                        gender = a.gender,
                        size = a.size,
                        description = a.description,
                        status = a.status,
                        health = a.health,
                        profileImage = a.profileImage
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v, nameError = if (v.isNotBlank()) null else "Obligatorio") }
    fun onSpeciesChange(v: String) = _state.update { it.copy(species = v) }
    fun onBreedChange(v: String) = _state.update { it.copy(breed = v, breedError = if (v.isNotBlank()) null else "Obligatorio") }
    fun onBirthDateChange(v: String) = _state.update { it.copy(birthDate = v) }
    fun onGenderChange(v: String) = _state.update { it.copy(gender = v) }
    fun onSizeChange(v: String) = _state.update { it.copy(size = v) }
    fun onStatusChange(v: String) = _state.update { it.copy(status = v) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v) }
    fun onHealthChange(v: String) = _state.update { it.copy(health = v) }

    fun onPhotoFileSelected(file: PlatformFile?) {
        if (file == null) {
            _state.update { it.copy(previewBytes = null, previewFileName = null) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(previewBytes = file.readBytes(), previewFileName = file.name) }
        }
    }

    fun confirmPhoto() {
        val bytes = _state.value.previewBytes ?: return
        val fileName = _state.value.previewFileName ?: return
        val id = _state.value.animalId
        viewModelScope.launch {
            _state.update { it.copy(isUploadingPhoto = true) }
            try {
                val newUrl = updateAnimalPhoto(id, bytes, fileName)
                _state.update {
                    it.copy(
                        isUploadingPhoto = false,
                        isPhotoSuccess = true,
                        profileImage = newUrl,
                        previewBytes = null,
                        previewFileName = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isUploadingPhoto = false, errorMessage = e.message) }
            }
        }
    }

    fun onPhotoSuccessHandled() = _state.update { it.copy(isPhotoSuccess = false) }

    fun save() {
        if (_state.value.isLoading) return
        val s = _state.value
        val birthDateValue = s.birthDate.trim().ifBlank { null }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (s.isCreateMode) {
                    val newId = createAnimal(
                        s.name.trim(), s.species, s.breed.trim(),
                        birthDateValue, s.gender, s.size, s.description.trim(),
                        s.status, s.health.trim()
                    )
                    val pendingBytes = s.previewBytes
                    val pendingName = s.previewFileName
                    if (pendingBytes != null && pendingName != null) {
                        try {
                            updateAnimalPhoto(newId, pendingBytes, pendingName)
                        } catch (e: Exception) {
                            println("LOG [AnimalEditViewModel]: foto falló tras crear — ${e.message}")
                        }
                    }
                    _state.update { it.copy(isLoading = false, createdAnimalId = newId) }
                } else {
                    updateAnimal(
                        s.animalId, s.name.trim(), s.species, s.breed.trim(),
                        birthDateValue, s.gender, s.size, s.description.trim(),
                        s.status, s.health.trim()
                    )
                    _state.update { it.copy(isLoading = false, isUpdated = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun delete() {
        val id = _state.value.animalId.ifEmpty { return }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                deleteAnimal(id)
                _state.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
