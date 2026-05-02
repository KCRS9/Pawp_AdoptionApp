package ies.sequeros.dam.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.CreateCommentUseCase
import ies.sequeros.dam.application.usecases.DeleteCommentUseCase
import ies.sequeros.dam.application.usecases.DeletePostUseCase
import ies.sequeros.dam.application.usecases.GetCommentsUseCase
import ies.sequeros.dam.application.usecases.GetPostByIdUseCase
import ies.sequeros.dam.domain.models.Comment
import ies.sequeros.dam.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailState(
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val commentText: String = "",
    val isLoadingPost: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isSubmitting: Boolean = false,
    val showDeletePostDialog: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
)

class PostDetailViewModel(
    private val getPostById: GetPostByIdUseCase,
    private val getComments: GetCommentsUseCase,
    private val createComment: CreateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deletePost: DeletePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    fun load(postId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPost = true) }
            try {
                val post = getPostById(postId)
                _state.update { it.copy(isLoadingPost = false, post = post) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingPost = false, errorMessage = e.message) }
            }
        }
        loadComments(postId)
    }

    fun loadComments(postId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingComments = true) }
            try {
                val comments = getComments(postId)
                _state.update { it.copy(isLoadingComments = false, comments = comments) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingComments = false) }
            }
        }
    }

    // ── Comentario ────────────────────────────────────────────────────────────

    fun onCommentTextChange(text: String) = _state.update { it.copy(commentText = text) }

    fun submitComment() {
        val postId = _state.value.post?.id ?: return
        val text   = _state.value.commentText.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                val comment = createComment(postId, text)
                _state.update { s ->
                    s.copy(
                        isSubmitting = false,
                        commentText  = "",
                        comments     = s.comments + comment,
                        post         = s.post?.copy(comments = s.post.comments + 1)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            try {
                deleteCommentUseCase(commentId)
                _state.update { s ->
                    s.copy(
                        comments = s.comments.filter { it.id != commentId },
                        post     = s.post?.copy(comments = maxOf(0, (s.post.comments) - 1))
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // ── Eliminar publicación ──────────────────────────────────────────────────

    fun requestDeletePost() = _state.update { it.copy(showDeletePostDialog = true) }
    fun dismissDeletePost() = _state.update { it.copy(showDeletePostDialog = false) }

    fun confirmDeletePost() {
        val postId = _state.value.post?.id ?: return
        _state.update { it.copy(showDeletePostDialog = false) }
        viewModelScope.launch {
            try {
                deletePost(postId)
                _state.update { it.copy(isDeleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun onErrorHandled() = _state.update { it.copy(errorMessage = null) }
}
