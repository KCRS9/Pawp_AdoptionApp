package ies.sequeros.dam.domain.models

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Animal(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val birthDate: String?,
    val gender: String,
    val size: String,
    val description: String,
    val health: String,
    val status: String,
    val shelterId: String,
    val shelterName: String,
    val locationName: String?,
    val profileImage: String?
)

@OptIn(ExperimentalTime::class)
fun String?.toAgeString(): String {
    if (isNullOrBlank()) return "Desconocida"
    return try {
        val parts = this!!.split("-")
        if (parts.size < 3) return "Desconocida"
        val bYear = parts[0].toInt()
        val bMonth = parts[1].toInt()
        val bDay = parts[2].toInt()

        val nowDays = (Clock.System.now().epochSeconds / 86400L).toInt()
        val birthDays = julianDay(bYear, bMonth, bDay) - julianDay(1970, 1, 1)
        val ageDays = nowDays - birthDays

        var years = ageDays / 365
        var months = (ageDays % 365) / 30
        if (months >= 12) { years++; months = 0 }

        when {
            years == 0 && months == 0 -> "Menos de 1 mes"
            years == 0 -> if (months == 1) "1 mes" else "$months meses"
            months == 0 -> if (years == 1) "1 año" else "$years años"
            else -> "${if (years == 1) "1 año" else "$years años"} y ${if (months == 1) "1 mes" else "$months meses"}"
        }
    } catch (e: Exception) {
        "Desconocida"
    }
}

private fun julianDay(year: Int, month: Int, day: Int): Int {
    val a = (14 - month) / 12
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
}
