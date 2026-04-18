package ies.sequeros.dam.ui.settings.changeEmail

data class ChangeEmailState(
    val newEmail: String = "",
    val password: String = "",

    val emailError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean

        get() = newEmail.isNotBlank() &&
                password.isNotBlank() &&
                emailError == null
}