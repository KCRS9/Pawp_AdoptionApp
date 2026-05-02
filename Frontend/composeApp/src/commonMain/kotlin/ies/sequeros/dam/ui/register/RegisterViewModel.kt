package ies.sequeros.dam.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.comandos.RegisterCommand
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import ies.sequeros.dam.utils.ValidationUtils
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
                email = email,
                emailError = ValidationUtils.emailError(email)
            )

        }
        validateForm()
    }

    fun onPasswordChange(password: String){

        _state.update {

            it.copy(
                password = password,
                passwordError = ValidationUtils.passwordErrorStrong(password),
                confirmPasswordError = when {
                    it.confirmPassword.isEmpty() -> null
                    it.confirmPassword == password -> null
                    else -> "Las contraseñas no coinciden"
                }
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

    fun onIsShelterChange(value: Boolean) {

        _state.update { it.copy(isShelter = value) }
        validateForm()
    }

    fun onShelterNameChange(value: String) {

        _state.update {
            it.copy(
                shelterName = value,
                shelterNameError = if (value.length >= 2) null else "Mínimo 2 caracteres"
            )
        }
        validateForm()
    }

    fun onShelterDescriptionChange(value: String) {

        _state.update { it.copy(shelterDescription = value) }
        validateForm()
    }

    fun onShelterPhoneChange(value: String) {

        _state.update {
            it.copy(
                shelterPhone = value,
                shelterPhoneError = if (value.length >= 9) null else "Teléfono no válido"
            )
        }
        validateForm()
    }

    fun onShelterEmailChange(value: String) {

        _state.update {
            it.copy(
                shelterEmail = value,
                shelterEmailError = ValidationUtils.emailError(value)
            )
        }
        validateForm()
    }

    private fun validateForm() {

        val s = _state.value

        // si es protectora, todos sus campos deben ser validos tambien
        val shelterValid = if (s.isShelter) {
            s.shelterName.isNotBlank() &&
                    s.shelterDescription.isNotBlank() &&
                    s.shelterPhone.isNotBlank() &&
                    s.shelterEmail.isNotBlank() &&
                    s.shelterNameError == null &&
                    s.shelterPhoneError == null &&
                    s.shelterEmailError == null
        } else true

        _state.update {
            it.copy(
                isValid = s.name.isNotBlank()            &&
                        s.email.isNotBlank()           &&
                        s.password.isNotBlank()        &&
                        s.confirmPassword.isNotBlank() &&
                        s.locationId != null           &&
                        s.nameError == null            &&
                        s.emailError == null           &&
                        s.passwordError == null        &&
                        s.confirmPasswordError == null &&
                        s.locationError == null        &&
                        shelterValid
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

                        name               = _state.value.name,
                        email              = _state.value.email,
                        password           = _state.value.password,
                        location           = _state.value.locationId!!,
                        shelterName        = _state.value.shelterName.ifBlank { null },
                        shelterDescription = _state.value.shelterDescription.ifBlank { null },
                        shelterPhone       = _state.value.shelterPhone.ifBlank { null },
                        shelterEmail       = _state.value.shelterEmail.ifBlank { null }
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