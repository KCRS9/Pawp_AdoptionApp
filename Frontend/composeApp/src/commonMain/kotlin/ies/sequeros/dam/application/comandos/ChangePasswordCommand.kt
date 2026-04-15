package ies.sequeros.dam.application.comandos

data class ChangePasswordCommand(

    val oldPassword: String,
    val newPassword: String
)