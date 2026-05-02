package ies.sequeros.dam.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetUserByIdUseCase
import ies.sequeros.dam.application.usecases.GetUserFavoritesUseCase
import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileState(
    val user: User? = null,
    val favoriteAnimals: List<AnimalSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserProfileViewModel(
    private val getUserById: GetUserByIdUseCase,
    private val getUserFavorites: GetUserFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = getUserById(userId)
                val favorites = try { getUserFavorites(userId) } catch (e: Exception) { emptyList() }
                _state.update { it.copy(user = user, favoriteAnimals = favorites, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
