package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.ICommentRepository

class DeleteCommentUseCase(private val repo: ICommentRepository) {
    suspend operator fun invoke(commentId: Int) = repo.deleteComment(commentId)
}
