package ies.sequeros.dam.domain.models

data class User(
    
    val id: String,
    val name: String,
    val email: String,
    val location: String,
    val role: String,
    val profileImage: String?,
    val shelterId: String?
)