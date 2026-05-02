package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.Comment
import ies.sequeros.dam.domain.repositories.ICommentRepository
import ies.sequeros.dam.infrastructure.dtos.CommentDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.parameters

class RestCommentRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : ICommentRepository {

    private fun String?.withBase() =
        if (this != null && !startsWith("http")) "$baseUrl$this" else this

    override suspend fun getComments(postId: Int): List<Comment> {
        val dtos: List<CommentDto> = client.get("$baseUrl/posts/$postId/comments").body()
        return dtos.map { it.toDomain() }
    }

    override suspend fun createComment(postId: Int, text: String): Comment {
        val dto: CommentDto = client.submitForm(
            url = "$baseUrl/posts/$postId/comments",
            formParameters = parameters { append("text", text) }
        ).body()
        return dto.toDomain()
    }

    override suspend fun deleteComment(commentId: Int): Boolean {
        val response = client.delete("$baseUrl/posts/comments/$commentId")
        return response.status.value in 200..299
    }

    private fun CommentDto.toDomain() = Comment(
        id        = id,
        userId    = userId,
        userName  = userName,
        userImage = userImage.withBase(),
        text      = text,
        date      = date
    )
}
