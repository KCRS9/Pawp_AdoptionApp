package ies.sequeros.dam.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetUsersUseCase
import ies.sequeros.dam.domain.models.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUsersState(
    val users: List<User> = emptyList(),
    val search: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class AdminUsersViewModel(
    private val getUsers: GetUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUsersState())
    val state: StateFlow<AdminUsersState> = _state.asStateFlow()

    private val _searchFlow = MutableStateFlow("")

    init {
        loadUsers()
        viewModelScope.launch {
            _searchFlow
                .debounce(400)
                .distinctUntilChanged()
                .collect { query -> loadUsers(query) }
        }
    }

    fun onSearchChange(value: String) {
        _state.update { it.copy(search = value) }
        _searchFlow.value = value
    }

    private fun loadUsers(search: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val users = getUsers(search = search?.ifBlank { null })
                _state.update { it.copy(users = users, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
