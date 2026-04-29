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

private const val PAGE_SIZE = 20

data class SocialState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null
)

class SocialViewModel(
    private val getPosts: GetPostsUseCase,
    private val likePost: LikePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SocialState())
    val state: StateFlow<SocialState> = _state.asStateFlow()

    init { loadFirst() }

    fun refresh() { loadFirst() }

    fun loadMore() {
        val s = _state.value
        if (s.isLoadingMore || !s.hasMore || s.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                val more = getPosts(skip = s.posts.size, limit = PAGE_SIZE).shuffled()
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        posts = it.posts + more,
                        hasMore = more.size == PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingMore = false, errorMessage = e.message) }
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

    private fun loadFirst() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, posts = emptyList(), hasMore = true) }
            try {
                val posts = getPosts(skip = 0, limit = PAGE_SIZE).shuffled()
                _state.update { it.copy(isLoading = false, posts = posts, hasMore = posts.size == PAGE_SIZE) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
