package ies.sequeros.dam.domain.repositories

import ies.sequeros.dam.domain.models.LikeResult
import ies.sequeros.dam.domain.models.Post

interface IPostRepository {
    suspend fun getPosts(skip: Int, limit: Int, userId: String? = null): List<Post>
    suspend fun getPostsByShelter(shelterId: String, skip: Int = 0, limit: Int = 50): List<Post>
    suspend fun getPostById(postId: Int): Post
    suspend fun createPost(
        photoBytes: ByteArray,
        photoName: String,
        text: String?,
        animalId: String?
    ): Post
    suspend fun toggleLike(postId: Int): LikeResult
    suspend fun deletePost(postId: Int)
}
