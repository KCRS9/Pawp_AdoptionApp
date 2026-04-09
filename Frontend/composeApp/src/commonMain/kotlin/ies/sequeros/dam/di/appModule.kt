package ies.sequeros.dam.di

import ies.sequeros.dam.application.usecases.LoginUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import ies.sequeros.dam.infrastructure.ktor.createHttpClient
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import ies.sequeros.dam.ui.appsettings.AppSettings
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.appsettings.UserSessionManager
import ies.sequeros.dam.ui.login.LoginViewModel
import ies.sequeros.dam.ui.register.RegisterViewModel
import org.koin.dsl.module

val appModule = module {

    val baseUrl = "https://localhost:8000"

    // --- Infraestructura ---
    single{

        TokenStorage(get())
    }

    single {

        val sessionManager: UserSessionManager = get()
        createHttpClient(
            tokenStorage = get(),
            onSessionExpired = {sessionManager.logout()}
        )
    }

    // --- Capa de aplicación ---
    single { UserSessionManager(get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }

    // --- Presentación ---

    single { AppSettings() }
    factory { AppViewModel(get(), get()) }
    factory { LoginViewModel( get(), get()) }
    factory { RegisterViewModel(get()) }
}