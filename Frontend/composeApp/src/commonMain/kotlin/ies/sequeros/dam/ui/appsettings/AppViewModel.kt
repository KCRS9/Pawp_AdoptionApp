package ies.sequeros.dam.ui.appsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(

    private val settings: AppSettings,
    private val sessionManager: UserSessionManager,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getLocalities: GetLocalitiesUseCase

): ViewModel() {

    // Tema de la app
    val themeMode = settings.themeMode

    fun setThemeMode(mode: ThemeMode) = settings.setThemeMode(mode)

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

                //Traemos el nombre de la localidad
                val locationName = try{

                    getLocalities().find { it.id == user.location }?.name

                }catch (e: Exception){

                    println("LOG[AppViewModel]: Error al traer el nombre de la localidad -> ${e.message}")
                }

                settings.saveUserProfile(user.copy(locationName = locationName.toString()))

            }catch (e: Exception){

                println("LOG [AppViewModel]: Error al cargar perfil → ${e.message}")
            }
        }
    }

    // Función para recargar el usuario
    fun refreshCurrentUser() = fetchCurrentUser()
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