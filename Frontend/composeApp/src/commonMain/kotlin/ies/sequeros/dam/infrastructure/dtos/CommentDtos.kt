package ies.sequeros.dam.infrastructure.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Int,
    @SerialName("user_id")    val userId: String,
    @SerialName("user_name")  val userName: String,
    @SerialName("user_image") val userImage: String? = null,
    val text: String,
    val date: String
)
