package ies.sequeros.dam.di

import ies.sequeros.dam.application.usecases.ChangeEmailUseCase
import ies.sequeros.dam.application.usecases.ChangePasswordUseCase
import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.GetShelterByIdUseCase
import ies.sequeros.dam.application.usecases.GetSheltersUseCase
import ies.sequeros.dam.application.usecases.UpdateShelterLogoUseCase
import ies.sequeros.dam.application.usecases.LoginUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import ies.sequeros.dam.application.usecases.UpdateAvatarUseCase
import ies.sequeros.dam.application.usecases.UpdateProfileUseCase
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.domain.repositories.ILocalityRepository
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.RestAuthRepository
import ies.sequeros.dam.infrastructure.RestLocalityRepository
import ies.sequeros.dam.infrastructure.RestShelterRepository
import ies.sequeros.dam.infrastructure.RestUserRepository
import ies.sequeros.dam.infrastructure.ktor.createHttpClient
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import ies.sequeros.dam.ui.appsettings.AppSettings
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.appsettings.UserSessionManager
import ies.sequeros.dam.ui.login.LoginViewModel
import ies.sequeros.dam.ui.profile.EditProfileViewModel
import ies.sequeros.dam.ui.shelters.ProtectorasViewModel
import ies.sequeros.dam.ui.register.RegisterViewModel
import ies.sequeros.dam.ui.settings.changeEmail.ChangeEmailViewModel
import ies.sequeros.dam.ui.settings.changePassword.ChangePasswordViewModel
import ies.sequeros.dam.ui.shelters.shelterEdit.ShelterEditViewModel
import ies.sequeros.dam.ui.shelters.shelterProfile.ShelterProfileViewModel
import org.koin.core.module.dsl.viewModel
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
    single<IShelterRepository> { RestShelterRepository(get(), baseUrl) }

    // --- Capa de aplicación ---
    single { UserSessionManager(get()) }

    // --- Casos de uso ---
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetLocalitiesUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { UpdateAvatarUseCase(get()) }
    factory { ChangePasswordUseCase(get()) }
    factory { ChangeEmailUseCase(get()) }
    factory { EditProfileViewModel(get(), get(), get(), get()) }
    factory { ChangePasswordViewModel(get()) }
    factory { ChangeEmailViewModel(get()) }
    factory { GetSheltersUseCase(get()) }
    factory { GetShelterByIdUseCase(get()) }
    factory { UpdateShelterLogoUseCase(get()) }

    // --- Presentación ---
    // get() resuelve la instancia de Settings registrada por cada plataforma
    single { AppSettings(get()) }
    factory { AppViewModel(get(), get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get(), get()) }

    viewModel { ProtectorasViewModel(get()) }
    viewModel { ShelterProfileViewModel(get()) }
    viewModel { ShelterEditViewModel(get(), get()) }
}