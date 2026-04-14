package ies.sequeros.dam.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.RegisterCommand
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(

    private val registerUseCase: RegisterUseCase,
    private val getLocalitiesUseCase: GetLocalitiesUseCase

): ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    init {

        loadLocalities()
    }

    private fun loadLocalities(){

        viewModelScope.launch {

            _state.update { it.copy(isLoadingLocalities = true) }

            try{

                val localities = getLocalitiesUseCase()
                _state.update { it.copy(localities = localities, isLoadingLocalities = false) }

                println("LOG[RegisterViewModel]: ${localities.size} cargadas")

            }catch (e: Exception){

                println("LOG [RegisterViewModel]: Error al cargar localidades → ${e.message}")
                _state.update { it.copy(isLoadingLocalities = false) }
            }
        }
    }

    fun onNameChange(name: String){
        _state.value = _state.value.copy(
            name = name,
            nameError = if (name.length >= 2) null else "Mínimo 2 caracteres"
        )

        validateForm()
    }

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(
                email      = email,
                emailError = if (email.contains("@")) null else "Email no válido"
            )
        }
        validateForm()
    }

    fun onPasswordChange(password: String){
        _state.update {
            it.copy(
                password = password,
                passwordError = if (password.length >= 6) null else "Mínimo 6 caracteres",
                confirmPasswordError = if (it.confirmPassword == password) null else "Las contraseñas no coinciden"
            )
        }
        validateForm()
    }

    fun onConfirmPasswordChange(confirmPassword: String){
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword == it.password) null else "Las contraseñas no coinciden"
            )
        }
        validateForm()
    }

    fun onLocationSelect(id: Int, name: String) {
        _state.update {
            it.copy(
                locationId = id,
                locationName = name,
                locationError = null
            )
        }
        validateForm()
    }

    private fun validateForm(){

        val s = _state.value
        _state.update {
            it.copy(
                isValid =   s.name.isNotBlank() &&
                            s.email.isNotBlank()&&
                            s.password.isNotBlank()&&
                            s.confirmPassword.isNotBlank()&&
                            s.locationId != null &&
                            s.nameError == null &&
                            s.emailError == null &&
                            s.passwordError == null &&
                            s.confirmPasswordError == null &&
                            s.locationError == null
            )
        }
    }

    fun register(){

        if(_state.value.isLoading) return

        viewModelScope.launch {
            _state.update {it.copy( isLoading = true, errorMessage = null)}

            println("LOG [RegisterViewModel]: Intentando registro para ${_state.value.email}")

            try{

                registerUseCase(
                    RegisterCommand(
                        name = _state.value.name,
                        email = _state.value.email,
                        password = state.value.password,
                        location = _state.value.locationId!!
                    )
                )

                println("LOG [RegisterViewModel]: Registro exitoso")

                _state.update { it.copy(isLoading = false, isRegisterSuccess = true) }

            }catch (e: Exception){

                println("LOG [RegisterViewModel]: Error en registro → ${e.message}")

                _state.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccess = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
    fun resetState() {
        _state.value = RegisterState()
    }
    
}