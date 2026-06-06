package ies.sequeros.dam.infrastructure

import com.russhwolf.settings.Settings
import ies.sequeros.dam.domain.models.Message
import ies.sequeros.dam.domain.repositories.IMessagesRepository
import ies.sequeros.dam.utils.MessagesConstants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalMessagesRepository(
    private val settings: Settings
) : IMessagesRepository {

    private val messagesKey = "messages_list"
    private val json = Json

    override suspend fun getMessages(): List<Message> {
        val stored = settings.getStringOrNull(messagesKey)
        return if (stored != null) {
            try {
                json.decodeFromString<List<Message>>(stored)
            } catch (e: Exception) {
                listOf(MessagesConstants.WELCOME_MESSAGE)
            }
        } else {
            listOf(MessagesConstants.WELCOME_MESSAGE)
        }
    }

    override suspend fun sendMessage(recipientId: Int, recipientName: String, text: String): Message {
        val messages = getMessages().toMutableList()
        val newMessage = Message(
            id = (messages.maxOfOrNull { it.id } ?: 0) + 1,
            recipientId = recipientId,
            recipientName = recipientName,
            recipientLogo = null,
            text = text,
            timestamp = "Hoy",
            isAdmin = false
        )

        messages.add(0, newMessage)
        settings.putString(messagesKey, json.encodeToString(messages))

        return newMessage
    }

    override fun clearMessages() {
        settings.remove(messagesKey)
    }
}
