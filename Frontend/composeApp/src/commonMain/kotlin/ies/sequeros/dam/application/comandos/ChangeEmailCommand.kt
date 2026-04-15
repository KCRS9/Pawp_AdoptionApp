package ies.sequeros.dam.application.comandos

data class ChangeEmailCommand(

    val newEmail: String,
    val password: String
)