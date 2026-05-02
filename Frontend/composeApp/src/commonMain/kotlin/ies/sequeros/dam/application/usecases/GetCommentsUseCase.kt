package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.ICommentRepository

class GetCommentsUseCase(private val repo: ICommentRepository) {
    suspend operator fun invoke(postId: Int) = repo.getComments(postId)
}
