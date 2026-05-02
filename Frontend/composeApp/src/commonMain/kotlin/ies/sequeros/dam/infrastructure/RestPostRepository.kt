package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.LikeResult
import ies.sequeros.dam.domain.models.Post
import ies.sequeros.dam.domain.repositories.IPostRepository
import ies.sequeros.dam.infrastructure.dtos.LikeResponseDto
import ies.sequeros.dam.infrastructure.dtos.PostDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

class RestPostRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : IPostRepository {

    private fun String?.withBase() =
        if (this != null && !this.startsWith("http")) "$baseUrl$this" else this

    override suspend fun getPosts(skip: Int, limit: Int, userId: String?): List<Post> {
        val dtos: List<PostDto> = client.get("$baseUrl/posts/") {
            parameter("skip", skip)
            parameter("limit", limit)
            userId?.let { parameter("user_id", it) }
        }.body()
        return dtos.map { it.toDomain() }
    }

    override suspend fun getPostById(postId: Int): Post {
        val dto: PostDto = client.get("$baseUrl/posts/$postId").body()
        return dto.toDomain()
    }

    override suspend fun createPost(
        photoBytes: ByteArray,
        photoName: String,
        text: String?,
        animalId: String?
    ): Post {
        val ext = photoName.substringAfterLast(".").lowercase()
        val contentType = when (ext) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png"         -> ContentType.Image.PNG
            "webp"        -> ContentType("image", "webp")
            else          -> ContentType.Image.JPEG
        }
        val response = client.post("$baseUrl/posts/") {
            setBody(MultiPartFormDataContent(formData {
                append("photo", photoBytes, Headers.build {
                    append(HttpHeaders.ContentType, contentType.toString())
                    append(HttpHeaders.ContentDisposition, "filename=\"$photoName\"")
                })
                text?.let { append("text", it) }
                animalId?.let { append("animal_id", it) }
            }))
        }
        if (!response.status.isSuccess())
            throw Exception("Error al crear la publicación (${response.status.value})")
        return response.body<PostDto>().toDomain()
    }

    override suspend fun toggleLike(postId: Int): LikeResult {
        val dto: LikeResponseDto = client.post("$baseUrl/posts/$postId/like").body()
        return LikeResult(dto.likes, dto.likedByMe)
    }

    override suspend fun deletePost(postId: Int) {
        val response = client.delete("$baseUrl/posts/$postId")
        if (!response.status.isSuccess())
            throw Exception("Error al eliminar la publicación (${response.status.value})")
    }

    private fun PostDto.toDomain() = Post(
        id         = id,
        userId     = user,
        userName   = userName,
        userImage  = userImage.withBase(),
        animalId   = animal,
        animalName = animalName,
        text       = text,
        photoUrl   = photo.withBase() ?: photo,
        createdAt  = createdAt,
        likes      = likes,
        likedByMe  = likedByMe,
        comments   = comments
    )
}
