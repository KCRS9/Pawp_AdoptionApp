package ies.sequeros.dam.ui.settings.changeEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.ChangeEmailCommand
import ies.sequeros.dam.application.usecases.ChangeEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeEmailViewModel(
    private val changeEmailUseCase: ChangeEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChangeEmailState())
    val state: StateFlow<ChangeEmailState> = _state.asStateFlow()

    fun onEmailChange(value: String) {

        _state.update {
            it.copy(
                newEmail   = value,
                emailError = if (value.contains("@")) null else "Email no válido"
            )
        }
    }

    fun onPasswordChange(value: String) {

        _state.update { it.copy(password = value) }
    }

    fun changeEmail() {

        if (_state.value.isLoading) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                changeEmailUseCase(
                    ChangeEmailCommand(
                        newEmail = _state.value.newEmail.trim(),
                        password = _state.value.password
                    )
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {

                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}