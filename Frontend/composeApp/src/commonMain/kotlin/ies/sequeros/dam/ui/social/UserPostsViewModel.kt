package ies.sequeros.dam.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetPostsUseCase
import ies.sequeros.dam.application.usecases.LikePostUseCase
import ies.sequeros.dam.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserPostsState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false
)

class UserPostsViewModel(
    private val getPosts: GetPostsUseCase,
    private val likePost: LikePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserPostsState())
    val state: StateFlow<UserPostsState> = _state.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, posts = emptyList()) }
            try {
                val posts = getPosts(skip = 0, limit = 100, userId = userId)
                _state.update { it.copy(isLoading = false, posts = posts) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleLike(postId: Int) {
        _state.update { s ->
            s.copy(posts = s.posts.map { post ->
                if (post.id != postId) post
                else {
                    val newLiked = !post.likedByMe
                    post.copy(
                        likedByMe = newLiked,
                        likes = if (newLiked) post.likes + 1 else maxOf(0, post.likes - 1)
                    )
                }
            })
        }
        viewModelScope.launch {
            try {
                val result = likePost(postId)
                _state.update { s ->
                    s.copy(posts = s.posts.map { post ->
                        if (post.id == postId) post.copy(likes = result.likes, likedByMe = result.likedByMe)
                        else post
                    })
                }
            } catch (e: Exception) {
                _state.update { s ->
                    s.copy(posts = s.posts.map { post ->
                        if (post.id != postId) post
                        else {
                            val reverted = !post.likedByMe
                            post.copy(
                                likedByMe = reverted,
                                likes = if (reverted) post.likes + 1 else maxOf(0, post.likes - 1)
                            )
                        }
                    })
                }
            }
        }
    }
}
