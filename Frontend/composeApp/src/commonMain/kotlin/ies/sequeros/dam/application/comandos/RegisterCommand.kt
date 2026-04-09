package ies.sequeros.dam.application.comandos

data class RegisterCommand(

    val name: String,
    val email: String,
    val password: String,
    val location: String,
    val role: String = "user"
)