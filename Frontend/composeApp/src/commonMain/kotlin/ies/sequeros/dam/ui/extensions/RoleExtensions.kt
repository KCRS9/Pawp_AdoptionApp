package ies.sequeros.dam.ui.extensions

// Convertir roles
fun String.toRoleLabel(): String = when (this) {
    
    "admin" -> "Administrador"
    "shelter" -> "Protectora"
    else -> "Protector"
}

// Convertir titulos
fun String.toTitleCase(): String =

    split(" ").joinToString( " ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }