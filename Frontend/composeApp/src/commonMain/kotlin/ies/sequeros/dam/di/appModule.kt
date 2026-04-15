package ies.sequeros.dam.di

import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.LoginUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.domain.repositories.ILocalityRepository
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.RestAuthRepository
import ies.sequeros.dam.infrastructure.RestLocalityRepository
import ies.sequeros.dam.infrastructure.RestUserRepository
import ies.sequeros.dam.infrastructure.ktor.createHttpClient
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import ies.sequeros.dam.ui.appsettings.AppSettings
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.appsettings.UserSessionManager
import ies.sequeros.dam.ui.login.LoginViewModel
import ies.sequeros.dam.ui.register.RegisterViewModel
import org.koin.dsl.module

val appModule = module {

    //val baseUrl = "http://10.0.2.2:8000"
    val baseUrl = "http://localhost:8000"
    //val baseUrl = "http://192.168.18.13:8000"

    // --- Infraestructura ---
    single {
        val sessionManager: UserSessionManager = get()
        createHttpClient(
            tokenStorage = get(),
            onSessionExpired = {sessionManager.logout()}
        )
    }

    single{ TokenStorage(get()) }
    single<IAuthRepository> { RestAuthRepository(get(), get(), baseUrl) }
    single<IUserRepository> { RestUserRepository(get(), baseUrl) }
    single<ILocalityRepository> { RestLocalityRepository(get(), baseUrl) }

    // --- Capa de aplicación ---
    single { UserSessionManager(get()) }

    // --- Casos de uso ---
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetLocalitiesUseCase(get()) }

    // --- Presentación ---
    single { AppSettings() }
    factory { AppViewModel(get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get(), get()) }
}