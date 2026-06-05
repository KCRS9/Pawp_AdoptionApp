package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.models.Message
import ies.sequeros.dam.domain.repositories.IMessagesRepository

class GetMessagesUseCase(private val repository: IMessagesRepository) {
    suspend operator fun invoke(): List<Message> = repository.getMessages()
}
