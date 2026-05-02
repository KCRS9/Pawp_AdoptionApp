package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.ICommentRepository

class CreateCommentUseCase(private val repo: ICommentRepository) {
    suspend operator fun invoke(postId: Int, text: String) = repo.createComment(postId, text)
}
