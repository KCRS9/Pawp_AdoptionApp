package ies.sequeros.dam.fakes

import ies.sequeros.dam.domain.models.LikeResult
import ies.sequeros.dam.domain.models.Post
import ies.sequeros.dam.domain.repositories.IPostRepository

// Implementación falsa de IPostRepository para usar en tests.
// shouldFailOnLike controla si queremos simular un fallo al dar like,
// lo que nos permite probar que el ViewModel revierte el cambio optimista.
class FakePostRepository(private val shouldFailOnLike: Boolean = false) : IPostRepository {

    // Post de referencia que devuelve getPosts — sin like dado y con 3 likes
    val testPost = Post(
        id = 1,
        userId = "user-001",
        userName = "Test User",
        userImage = null,
        animalId = null,
        animalName = null,
        text = "Post de prueba",
        photoUrl = "/static/images/posts/test.jpg",
        createdAt = "2025-01-01T00:00:00",
        likes = 3,
        likedByMe = false,
        comments = 0
    )

    override suspend fun getPosts(skip: Int, limit: Int, userId: String?) = listOf(testPost)

    override suspend fun toggleLike(postId: Int): LikeResult {
        if (shouldFailOnLike) throw Exception("Error de red")
        // Simula que el servidor confirma el like con el nuevo conteo
        return LikeResult(likes = 4, likedByMe = true)
    }

    // Métodos que no usamos en los tests actuales
    override suspend fun getPostsByShelter(shelterId: String, skip: Int, limit: Int) = emptyList<Post>()
    override suspend fun getPostById(postId: Int): Post = throw NotImplementedError()
    override suspend fun createPost(photoBytes: ByteArray, photoName: String, text: String?, animalId: String?): Post = throw NotImplementedError()
    override suspend fun deletePost(postId: Int) = Unit
}
