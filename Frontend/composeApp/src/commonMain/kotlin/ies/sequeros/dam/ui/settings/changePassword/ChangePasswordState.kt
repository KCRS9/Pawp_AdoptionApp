package ies.sequeros.dam.ui.settings.changePassword

data class ChangePasswordState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",

    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = oldPassword.isNotBlank() &&
                newPassword.length >= 6  &&
                newPassword == confirmPassword &&
                newPasswordError == null &&
                confirmPasswordError == null
}