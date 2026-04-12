package ies.sequeros.dam.ui.appsettings

import ies.sequeros.dam.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode{
    SYSTEM,
    LIGHT,
    DARK
}

class AppSettings {

    // TEMA DEL USUARIO
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode){

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