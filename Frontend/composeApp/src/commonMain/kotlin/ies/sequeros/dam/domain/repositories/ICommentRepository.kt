package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.Comment

interface ICommentRepository {
    suspend fun getComments(postId: Int): List<Comment>
    suspend fun createComment(postId: Int, text: String): Comment
    suspend fun deleteComment(commentId: Int): Boolean
}
