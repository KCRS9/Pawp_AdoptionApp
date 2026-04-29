package ies.sequeros.dam.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.CreatePostUseCase
import ies.sequeros.dam.application.usecases.GetAnimalsUseCase
import ies.sequeros.dam.domain.models.AnimalSummary
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostFormState(
    val photoBytes: ByteArray? = null,
    val photoName: String? = null,
    val text: String = "",
    val selectedAnimalId: String? = null,
    val selectedAnimalName: String? = null,
    val animals: List<AnimalSummary> = emptyList(),
    val animalsFilter: String = "",
    val isLoadingAnimals: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isPhotoSelected: Boolean get() = photoBytes != null
    val canPost: Boolean get() = isPhotoSelected && !isLoading
}

class PostFormViewModel(
    private val createPost: CreatePostUseCase,
    private val getAnimals: GetAnimalsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostFormState())
    val state: StateFlow<PostFormState> = _state.asStateFlow()

    fun reset() {
        _state.value = PostFormState()
    }

    fun onPhotoSelected(file: PlatformFile?) {
        if (file == null) {
            _state.update { it.copy(photoBytes = null, photoName = null) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(photoBytes = file.readBytes(), photoName = file.name) }
        }
    }

    fun onTextChange(v: String) = _state.update { it.copy(text = v) }

    fun loadAnimals() {
        if (_state.value.animals.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAnimals = true) }
            try {
                val animals = getAnimals(skip = 0, limit = 100, species = null)
                _state.update { it.copy(isLoadingAnimals = false, animals = animals) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingAnimals = false) }
            }
        }
    }

    fun onAnimalsFilterChange(v: String) = _state.update { it.copy(animalsFilter = v) }

    fun selectAnimal(id: String, name: String) =
        _state.update { it.copy(selectedAnimalId = id, selectedAnimalName = name, animalsFilter = "") }

    fun clearAnimal() =
        _state.update { it.copy(selectedAnimalId = null, selectedAnimalName = null) }

    fun post() {
        val s = _state.value
        val bytes = s.photoBytes ?: return
        val name = s.photoName ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                createPost(
                    photoBytes = bytes,
                    photoName  = name,
                    text       = s.text.trim().ifBlank { null },
                    animalId   = s.selectedAnimalId
                )
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onSuccessHandled() = _state.update { it.copy(isSuccess = false) }
}
