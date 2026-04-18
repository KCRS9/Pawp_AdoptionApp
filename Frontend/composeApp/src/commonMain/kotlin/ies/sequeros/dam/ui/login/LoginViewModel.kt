package ies.sequeros.dam.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.LoginCommand
import ies.sequeros.dam.application.usecases.LoginUseCase
import ies.sequeros.dam.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(

    private val loginUseCase: LoginUseCase

): ViewModel() {

//    init {
//        // Credenciales de desarrollo — eliminar antes de producción
//        onEmailChange("admin@admin.com")
//        onPasswordChange("123456789")
//    }

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> =_state.asStateFlow()

    fun onEmailChange(email: String){
        _state.update {
            it.copy(
                email = email,
                emailError = ValidationUtils.emailError(email),
                errorMessage = null
            )
        }

        validateForm()
    }

    fun onPasswordChange(password: String){

        _state.update {
            it.copy(
                password = password,
                passwordError = if (password.length >= 6) null else "Mínimo 6 caracteres",
                errorMessage = null
            )
        }
        validateForm()
    }
    private fun validateForm(){

        val s = _state.value

        _state.update {
            it.copy(
                isValid =   s.email.isNotBlank() &&
                        s.password.isNotBlank() &&
                        s.emailError == null &&
                        s.passwordError == null
            )
        }
    }


    fun login(){

        if(state.value.isLoading) return

        viewModelScope.launch {

            _state.update {
                it.copy(isLoading = true,
                    errorMessage = null)
            }

            println("LOG [LoginViewModel]: Intentando login con ${_state.value.email}")

            try{

                loginUseCase(
                    LoginCommand(
                        _state.value.email,
                        state.value.password
                    )
                )

                _state.update {

                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true
                    )
                }

                println("LOG [LoginViewModel]: Login exitoso")

            }catch (e: Exception){

                println("LOG [LoginViewModel]: Error en login → ${e.message}")

                _state.update {

                    it.copy(
                        isLoading = false,
                        isLoginSuccess = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }

    }

    fun resetState(){

        _state.value = LoginState()
    }

}