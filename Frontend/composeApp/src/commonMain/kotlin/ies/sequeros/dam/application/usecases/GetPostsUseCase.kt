package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IPostRepository

class GetPostsUseCase(private val repo: IPostRepository) {
    suspend operator fun invoke(skip: Int, limit: Int, userId: String? = null) =
        repo.getPosts(skip, limit, userId)
}
