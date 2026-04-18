package ies.sequeros.dam.ui.appsettings

import com.russhwolf.settings.Settings
import ies.sequeros.dam.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode{
    SYSTEM,
    LIGHT,
    DARK
}

// Recibimos la misma instancia de Settings que usa TokenStorage.
// Al arrancar la app leemos el tema guardado; al cambiarlo lo escribimos en disco.
class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEY_THEME = "theme_mode"
    }

    // TEMA DEL USUARIO
    private val _themeMode = MutableStateFlow(
        ThemeMode.valueOf(settings.getStringOrNull(KEY_THEME) ?: ThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode){
        settings.putString(KEY_THEME, mode.name)
        _themeMode.value = mode
    }

    // PERFIL AUTENTICADO
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun saveUserProfile(user: User){

        _currentUser.value = user
    }

    fun clearUserProfile(){

        _currentUser.value = null
    }
}