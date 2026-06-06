package ies.sequeros.dam.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val recipientId: Int,
    val recipientName: String,
    val recipientLogo: String? = null,
    val text: String,
    val timestamp: String,
    val isAdmin: Boolean = false
)
