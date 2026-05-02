package ies.sequeros.dam.domain.models

data class Post(
    val id: Int,
    val userId: String,
    val userName: String,
    val userImage: String?,
    val animalId: String?,
    val animalName: String?,
    val text: String?,
    val photoUrl: String,
    val createdAt: String,
    val likes: Int,
    val likedByMe: Boolean,
    val comments: Int = 0
)

data class LikeResult(val likes: Int, val likedByMe: Boolean)

fun String.toDisplayDate(): String = try {
    val datePart = substringBefore("T")
    val (year, month, day) = datePart.split("-")
    val months = listOf("", "ene", "feb", "mar", "abr", "may",
                        "jun", "jul", "ago", "sep", "oct", "nov", "dic")
    "${day.toInt()} ${months[month.toInt()]} $year"
} catch (_: Exception) { this }
