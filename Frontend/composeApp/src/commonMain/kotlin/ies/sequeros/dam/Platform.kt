package ies.sequeros.dam

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform