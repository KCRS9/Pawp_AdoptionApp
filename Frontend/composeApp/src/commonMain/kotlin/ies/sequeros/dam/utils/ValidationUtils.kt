package ies.sequeros.dam.utils

object ValidationUtils {

    private val EMAIL_REGEX = Regex(

        "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    )

    // Devuelve null si el campo está vacío (el botón se deshabilita por isValid, no por el error)
    // Devuelve mensaje de error si el formato es incorrecto
    fun emailError(email: String): String? = when {

        email.isEmpty() -> null
        !EMAIL_REGEX.matches(email) -> "Formato de correo no válido (ej: nombre@dominio.com)"
        else -> null
    }

    // Validación fuerte: para el formulario de registro y cambio de contraseña
    // Login usa solo longitud mínima para no bloquear contraseñas antiguas
    fun passwordErrorStrong(password: String): String? = when {

        password.isEmpty() -> null
        password.length < 8 -> "Mínimo 8 caracteres"
        !password.any { it.isUpperCase() } -> "Debe incluir al menos una mayúscula"
        !password.any { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }
}