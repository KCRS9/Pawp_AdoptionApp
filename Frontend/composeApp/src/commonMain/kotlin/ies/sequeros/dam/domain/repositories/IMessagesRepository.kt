package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.Message

interface IMessagesRepository {
    suspend fun getMessages(): List<Message>
    suspend fun sendMessage(recipientId: Int, recipientName: String, text: String): Message
    fun clearMessages()
}
