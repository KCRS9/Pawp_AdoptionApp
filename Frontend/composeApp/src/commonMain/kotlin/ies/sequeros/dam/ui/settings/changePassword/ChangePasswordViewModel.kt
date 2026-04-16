package ies.sequeros.dam.ui.settings.changePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.ChangePasswordCommand
import ies.sequeros.dam.application.usecases.ChangePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun onOldPasswordChange(value: String) {

        _state.update { it.copy(oldPassword = value) }
    }

    fun onNewPasswordChange(value: String) {

        _state.update {
            it.copy(
                newPassword = value,
                newPasswordError = if (value.length >= 6) null else "Mínimo 6 caracteres",
                confirmPasswordError = if (it.confirmPassword == value) null else "Las contraseñas no coinciden"
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {

        _state.update {
            it.copy(
                confirmPassword = value,
                confirmPasswordError = if (value == it.newPassword) null else "Las contraseñas no coinciden"
            )
        }
    }

    fun changePassword() {

        if (_state.value.isLoading) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                changePasswordUseCase(

                    ChangePasswordCommand(
                        oldPassword = _state.value.oldPassword,
                        newPassword = _state.value.newPassword
                    )
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {

                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}