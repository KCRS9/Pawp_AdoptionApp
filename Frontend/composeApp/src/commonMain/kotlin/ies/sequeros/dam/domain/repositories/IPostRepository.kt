package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.LikeResult
import ies.sequeros.dam.domain.models.Post

interface IPostRepository {
    suspend fun getPosts(skip: Int, limit: Int): List<Post>
    suspend fun createPost(
        photoBytes: ByteArray,
        photoName: String,
        text: String?,
        animalId: String?
    ): Post
    suspend fun toggleLike(postId: Int): LikeResult
}
