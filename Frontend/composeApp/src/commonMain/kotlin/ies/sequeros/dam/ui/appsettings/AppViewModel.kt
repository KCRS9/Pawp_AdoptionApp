package ies.sequeros.dam.ui.appsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AppViewModel(

    private val settings: AppSettings,
    private val sessionManager: UserSessionManager

): ViewModel() {

    val isDarkMode = settings.isDarkMode

    val isLoggedIn = sessionManager.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun toggleTheme() = settings.toggleDarkMode()

    fun notifyLogin() = sessionManager.notifyLogin()

    fun logout() = sessionManager.logout()

}