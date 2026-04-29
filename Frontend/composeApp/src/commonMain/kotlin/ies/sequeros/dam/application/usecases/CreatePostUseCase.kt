package ies.sequeros.dam.application.usecases

import ies.sequeros.dam.domain.repositories.IPostRepository

class CreatePostUseCase(private val repo: IPostRepository) {
    suspend operator fun invoke(
        photoBytes: ByteArray,
        photoName: String,
        text: String?,
        animalId: String?
    ) = repo.createPost(photoBytes, photoName, text, animalId)
}
