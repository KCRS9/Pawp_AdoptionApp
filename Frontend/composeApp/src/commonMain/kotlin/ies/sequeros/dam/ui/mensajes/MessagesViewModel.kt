package ies.sequeros.dam.ui.mensajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetMessagesUseCase
import ies.sequeros.dam.application.usecases.SendMessageUseCase
import ies.sequeros.dam.domain.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagesState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class MessagesViewModel(
    private val getMessages: GetMessagesUseCase,
    private val sendMessage: SendMessageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MessagesState())
    val state: StateFlow<MessagesState> = _state.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val messages = getMessages()
                _state.update { it.copy(messages = messages, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    fun sendMessageToShelter(recipientId: Int, recipientName: String, text: String) {
        viewModelScope.launch {
            try {
                val message = sendMessage(recipientId, recipientName, text)
                val updatedMessages = listOf(message) + _state.value.messages
                _state.update {
                    it.copy(
                        messages = updatedMessages,
                        successMessage = "Mensaje enviado"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}
