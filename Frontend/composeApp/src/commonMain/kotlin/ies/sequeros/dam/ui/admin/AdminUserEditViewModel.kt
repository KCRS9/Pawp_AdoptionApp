package ies.sequeros.dam.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetUserByIdUseCase
import ies.sequeros.dam.application.usecases.UpdateUserAdminUseCase
import ies.sequeros.dam.application.usecases.UpdateUserPhotoAdminUseCase
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUserEditState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "user",
    val location: Int = 1,
    val description: String = "",
    val profileImage: String? = null,
    val previewBytes: ByteArray? = null,
    val previewFileName: String? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AdminUserEditViewModel(
    private val getUserById: GetUserByIdUseCase,
    private val updateUserAdmin: UpdateUserAdminUseCase,
    private val updateUserPhoto: UpdateUserPhotoAdminUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUserEditState())
    val state: StateFlow<AdminUserEditState> = _state.asStateFlow()

    fun init(userId: String) {
        if (_state.value.userId == userId) return
        viewModelScope.launch {
            _state.update { it.copy(userId = userId, isLoading = true) }
            try {
                val user = getUserById(userId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = user.name,
                        email = user.email,
                        role = user.role,
                        location = user.location,
                        description = user.description ?: "",
                        profileImage = user.profileImage
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onSuccessHandled() = _state.update { it.copy(isSuccess = false) }

    fun onNameChange(v: String) = _state.update { it.copy(name = v) }
    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onRoleChange(v: String) = _state.update { it.copy(role = v) }
    fun onLocationChange(v: Int) = _state.update { it.copy(location = v) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v) }

    fun onPhotoSelected(file: PlatformFile?) {
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
        viewModelScope.launch {
            _state.update { it.copy(isUploadingPhoto = true) }
            try {
                val newUrl = updateUserPhoto(_state.value.userId, bytes, fileName)
                _state.update {
                    it.copy(
                        isUploadingPhoto = false,
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

    fun save() {
        val s = _state.value
        if (s.name.isBlank() || s.email.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                updateUserAdmin(s.userId, s.name.trim(), s.email.trim(),
                                s.role, s.location, s.description.ifBlank { null })
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
