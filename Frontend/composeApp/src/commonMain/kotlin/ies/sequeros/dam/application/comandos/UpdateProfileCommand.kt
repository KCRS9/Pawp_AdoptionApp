package ies.sequeros.dam.application.comandos

data class UpdateProfileCommand(

    val name: String? = null,
    val locationId: Int? = null,
    val description: String? = null
)