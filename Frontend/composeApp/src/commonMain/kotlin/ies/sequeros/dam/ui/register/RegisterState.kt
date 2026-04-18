package ies.sequeros.dam.ui.register

import ies.sequeros.dam.domain.models.Locality

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    //Listar localidades
    val locationId: Int? = null,
    val locationName: String = "",
    val localities: List<Locality> = emptyList(),
    val isLoadingLocalities: Boolean = false,

    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isValid: Boolean = false,

    // Errores por campo
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val locationError: String? = null,

    // Error del servidor
    val errorMessage: String? = null,

    // Campos para el registro de protectora
    // isShelter controla si se muestran los campos adicionales
    val isShelter: Boolean = false,
    val shelterName: String = "",
    val shelterDescription: String  = "",
    val shelterPhone: String = "",
    val shelterEmail: String = "",
    val shelterNameError: String? = null,
    val shelterPhoneError: String? = null,
    val shelterEmailError: String? = null
)