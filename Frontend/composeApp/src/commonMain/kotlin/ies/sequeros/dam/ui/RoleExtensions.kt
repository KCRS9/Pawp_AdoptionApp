package ies.sequeros.dam.ui

fun String.toRoleLabel(): String = when (this) {
    
    "admin" -> "Administrador"
    "shelter" -> "Protectora"
    else -> "Protector"
}