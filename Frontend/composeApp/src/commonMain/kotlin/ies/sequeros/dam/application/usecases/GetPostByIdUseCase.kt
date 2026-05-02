package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IPostRepository

class GetPostByIdUseCase(private val repo: IPostRepository) {
    suspend operator fun invoke(postId: Int) = repo.getPostById(postId)
}
