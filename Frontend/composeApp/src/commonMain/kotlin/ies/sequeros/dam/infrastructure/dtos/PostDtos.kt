package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int,
    val user: String,
    @SerialName("user_name")  val userName: String,
    @SerialName("user_image") val userImage: String? = null,
    val animal: String? = null,
    @SerialName("animal_name") val animalName: String? = null,
    val text: String? = null,
    val photo: String,
    @SerialName("created_at") val createdAt: String,
    val likes: Int,
    @SerialName("liked_by_me") val likedByMe: Boolean = false,
    @SerialName("comments")    val comments: Int = 0
)

@Serializable
data class LikeResponseDto(
    val likes: Int,
    @SerialName("liked_by_me") val likedByMe: Boolean
)
