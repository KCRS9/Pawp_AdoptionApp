package ies.sequeros.dam.ui.login

data class LoginState(

    val email: String = "admin@admin.com",
    val password: String = "12345678",

    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,


    val isValid: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,

    val errorMessage: String? = null
)