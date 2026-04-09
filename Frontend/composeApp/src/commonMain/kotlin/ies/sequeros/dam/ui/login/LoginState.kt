package ies.sequeros.dam.ui.login

data class LoginState(

    val email: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,


    val isValid: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,

    val errorMessage: String? = null
)