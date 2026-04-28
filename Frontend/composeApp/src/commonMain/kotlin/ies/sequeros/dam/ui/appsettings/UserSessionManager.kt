package ies.sequeros.dam.ui.appsettings

import ies.sequeros.dam.infrastructure.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionManager(

    private val tokenStorage: TokenStorage
) {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _sessionVersion = MutableStateFlow(0L)
    val sessionVersion: StateFlow<Long> = _sessionVersion.asStateFlow()

    init {

        checkSession()
    }

    fun checkSession(){

        val hasSession = tokenStorage.hasSession()
        println("LOG [UserSessionManager]: Comprobando sesión → hasSession = $hasSession")
        _isLoggedIn.value = hasSession
    }

    fun notifyLogin(){

        println("LOG [UserSessionManager]: Login notificado. Sesión activa.")
        _sessionVersion.value++
        _isLoggedIn.value = true
    }

    fun logout(){

        println("LOG [UserSessionManager]: Cerrando sesión.")
        tokenStorage.clear() // borra el JWT del almacenamiento persistente
        _isLoggedIn.value = false
    }
}