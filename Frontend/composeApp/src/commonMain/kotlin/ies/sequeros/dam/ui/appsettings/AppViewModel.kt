package ies.sequeros.dam.ui.appsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.dam.application.usecases.AddFavoriteUseCase
import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import ies.sequeros.dam.application.usecases.GetFavoritesUseCase
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.RemoveFavoriteUseCase
import ies.sequeros.dam.domain.models.AnimalSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(

    private val settings: AppSettings,
    private val sessionManager: UserSessionManager,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getLocalities: GetLocalitiesUseCase,
    private val getFavorites: GetFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase

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

    val sessionVersion = sessionManager.sessionVersion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0L
    )

    //Perfil de usuario
    val currentUser = settings.currentUser

    // Favoritos
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _favoriteAnimals = MutableStateFlow<List<AnimalSummary>>(emptyList())
    val favoriteAnimals: StateFlow<List<AnimalSummary>> = _favoriteAnimals.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = getFavorites()
                _favoriteIds.value = favorites.map { it.id }.toSet()
                _favoriteAnimals.value = favorites
            } catch (e: Exception) {
                println("LOG [AppViewModel]: Error al cargar favoritos → ${e.message}")
            }
        }
    }

    fun toggleFavorite(animalId: String) {
        val current = _favoriteIds.value
        val adding = animalId !in current
        _favoriteIds.value = if (adding) current + animalId else current - animalId
        viewModelScope.launch {
            try {
                if (adding) addFavorite(animalId) else removeFavorite(animalId)
                loadFavorites()
            } catch (e: Exception) {
                _favoriteIds.value = current
                println("LOG [AppViewModel]: Error al toggle favorito → ${e.message}")
            }
        }
    }

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
                    null
                }

                settings.saveUserProfile(user.copy(locationName = locationName.toString()))
                loadFavorites()

            }catch (e: Exception){

                println("LOG [AppViewModel]: Error al cargar perfil → ${e.message}")
            }
        }
    }

    // Limpiamos el perfil antes de refrescar para que la UI muestre
    // CircularProgressIndicator en vez de datos del usuario anterior.
    fun refreshCurrentUser() {
        settings.clearUserProfile()
        fetchCurrentUser()
    }

    fun notifyLogin() {
        settings.clearUserProfile()
        sessionManager.notifyLogin()
        fetchCurrentUser()
    }

    fun logout(){
        settings.clearUserProfile()
        _favoriteIds.value = emptySet()
        _favoriteAnimals.value = emptyList()
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
