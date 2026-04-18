package ies.sequeros.dam.ui.shelters.shelterEdit


data class ShelterEditState(
    
    val shelterId: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val description: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val isLoading: Boolean   = false,
    val isSuccess: Boolean   = false,
    val errorMessage: String? = null
) {
    // El botón de guardar se habilita cuando los campos obligatorios son válidos y sin error
    val isValid: Boolean
        get() = name.isNotBlank() &&
                phone.isNotBlank() &&
                email.isNotBlank() &&
                nameError == null &&
                phoneError == null &&
                emailError == null
}