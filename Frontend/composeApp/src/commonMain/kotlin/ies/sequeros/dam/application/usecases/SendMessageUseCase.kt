package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.Message
import ies.sequeros.dam.domain.repositories.IMessagesRepository

class SendMessageUseCase(private val repository: IMessagesRepository) {
    suspend operator fun invoke(recipientId: Int, recipientName: String, text: String): Message =
        repository.sendMessage(recipientId, recipientName, text)
}
