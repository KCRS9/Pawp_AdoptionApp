package ies.sequeros.dam.ui.appsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(

    private val settings: AppSettings,
    private val sessionManager: UserSessionManager,
    private val getCurrentUser: GetCurrentUserUseCase

): ViewModel() {

    // Tema de la app
    val themeMode = settings.themeMode

    fun setTheme(mode: ThemeMode) = settings.setThemeMode(mode)

    //Sesión
    val isLoggedIn = sessionManager.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    //Perfil de usuario

    val currentUser = settings.currentUser

    /* Llama a GET /users/me y guarda el resultado en AppSettings.
      Se invoca desde notifyLogin() y desde init.
     */
    fun fetchCurrentUser(){
        viewModelScope.launch {
            try {

                val user = getCurrentUser()
                settings.saveUserProfile(user)

            }catch (e: Exception){

                println("LOG [AppViewModel]: Error al cargar perfil → ${e.message}")
            }
        }
    }
    fun notifyLogin() {

        sessionManager.notifyLogin()
        fetchCurrentUser()
    }

    fun logout(){

        settings.clearUserProfile()
        sessionManager.logout()
    }

    init{
        // Si la app arranca con un token persistido, cargamos el perfil del servidor
        viewModelScope.launch {
            // filterNotNull() descarta el null inicial (estado "comprobando")
            // first() obtiene el primer valor no nulo y cancela la colección
            val loggedIn = isLoggedIn.filterNotNull().first()
            if(loggedIn) fetchCurrentUser()
        }
    }

}