package ies.sequeros.dam.utils

import ies.sequeros.dam.domain.models.Message

object MessagesConstants {
    val WELCOME_MESSAGE = Message(
        id = 0,
        recipientId = 0,
        recipientName = "PAWP ADMIN",
        recipientLogo = null,
        text = "¡Bienvenido a PAWP!\n\nAquí puedes enviar mensajes a las protectoras de animales para consultas y solicitudes.",
        timestamp = "Bienvenida",
        isAdmin = true
    )
}
