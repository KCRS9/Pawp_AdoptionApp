package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IPostRepository

class DeletePostUseCase(private val repo: IPostRepository) {
    suspend operator fun invoke(postId: Int) = repo.deletePost(postId)
}
