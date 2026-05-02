package ies.sequeros.dam.infrastructure.storage

import com.russhwolf.settings.Settings

// Gestiona el almacenamiento persistente del token JWT.
// Usa multiplatform-settings que internamente usa:
//   Android → SharedPreferences (cifrado con security-crypto)
//   Desktop  → java.util.prefs.Preferences
//   Web → localStorage del navegador
class TokenStorage(private val settings: Settings) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    // Guarda el token recibido del backend tras el login
    fun saveToken(accessToken: String) {
        println("LOG [TokenStorage]: Token guardado correctamente")
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
    }

    // devuelve el token guardado, o null si no hay sesion activa
    fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)

    // comprueba si hay sesion activa
    fun hasSession(): Boolean = getAccessToken() != null

    // Borra el token (logout)
    fun clear() {
        println("LOG [TokenStorage]: Token eliminado (logout)")
        settings.remove(KEY_ACCESS_TOKEN)
    }
}