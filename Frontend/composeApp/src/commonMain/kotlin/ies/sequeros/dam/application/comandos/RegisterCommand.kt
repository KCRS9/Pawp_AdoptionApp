package ies.sequeros.dam.application.comandos

data class RegisterCommand(

    val name: String,
    val email: String,
    val password: String,
    val location: Int,
    val shelterName: String?        = null,
    val shelterDescription: String? = null,
    val shelterPhone: String?       = null,
    val shelterEmail: String?       = null
)