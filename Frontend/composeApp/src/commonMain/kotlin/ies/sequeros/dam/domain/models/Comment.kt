package ies.sequeros.dam.domain.models

data class Comment(
    val id: Int,
    val userId: String,
    val userName: String,
    val userImage: String?,
    val text: String,
    val date: String
)
