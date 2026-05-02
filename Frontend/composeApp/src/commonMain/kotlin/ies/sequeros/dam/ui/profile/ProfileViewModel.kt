package ies.sequeros.dam.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetPostsUseCase
import ies.sequeros.dam.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfilePostsState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val getPosts: GetPostsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilePostsState())
    val state: StateFlow<ProfilePostsState> = _state.asStateFlow()

    fun loadPosts(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, posts = emptyList()) }
            try {
                val posts = getPosts(skip = 0, limit = 50, userId = userId)
                _state.update { it.copy(isLoading = false, posts = posts) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
