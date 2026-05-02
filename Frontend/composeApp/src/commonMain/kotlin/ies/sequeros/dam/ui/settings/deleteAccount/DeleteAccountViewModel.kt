package ies.sequeros.dam.ui.settings.deleteAccount

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DeleteAccountState(
    val email: String = "",
    val password: String = "",
    val showConfirmDialog: Boolean = false,
    val triggerLogout: Boolean = false
)

class DeleteAccountViewModel : ViewModel() {

    private val _state = MutableStateFlow(DeleteAccountState())
    val state: StateFlow<DeleteAccountState> = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v) }

    fun isValid(currentUserEmail: String?) =
        _state.value.email.isNotBlank() &&
        _state.value.password.isNotBlank() &&
        _state.value.email.trim() == currentUserEmail

    fun onDeleteClick() = _state.update { it.copy(showConfirmDialog = true) }
    fun onDismiss() = _state.update { it.copy(showConfirmDialog = false) }
    fun onConfirm() = _state.update { it.copy(showConfirmDialog = false, triggerLogout = true) }
}
