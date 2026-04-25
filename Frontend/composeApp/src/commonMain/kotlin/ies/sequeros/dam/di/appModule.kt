package ies.sequeros.dam.di

import ies.sequeros.dam.application.usecases.AddFavoriteUseCase
import ies.sequeros.dam.application.usecases.ChangeEmailUseCase
import ies.sequeros.dam.application.usecases.ChangePasswordUseCase
import ies.sequeros.dam.application.usecases.CreateAnimalUseCase
import ies.sequeros.dam.application.usecases.DeleteAnimalUseCase
import ies.sequeros.dam.application.usecases.GetAnimalByIdUseCase
import ies.sequeros.dam.application.usecases.GetAnimalsUseCase
import ies.sequeros.dam.application.usecases.GetCurrentUserUseCase
import ies.sequeros.dam.application.usecases.GetFavoritesUseCase
import ies.sequeros.dam.application.usecases.GetLocalitiesUseCase
import ies.sequeros.dam.application.usecases.GetShelterByIdUseCase
import ies.sequeros.dam.application.usecases.GetSheltersUseCase
import ies.sequeros.dam.application.usecases.GetUserByIdUseCase
import ies.sequeros.dam.application.usecases.GetUserFavoritesUseCase
import ies.sequeros.dam.application.usecases.GetUsersUseCase
import ies.sequeros.dam.application.usecases.RemoveFavoriteUseCase
import ies.sequeros.dam.application.usecases.UpdateAnimalPhotoUseCase
import ies.sequeros.dam.application.usecases.UpdateAnimalUseCase
import ies.sequeros.dam.application.usecases.UpdateShelterLogoUseCase
import ies.sequeros.dam.application.usecases.LoginUseCase
import ies.sequeros.dam.application.usecases.RegisterUseCase
import ies.sequeros.dam.application.usecases.UpdateAvatarUseCase
import ies.sequeros.dam.application.usecases.UpdateProfileUseCase
import ies.sequeros.dam.application.usecases.UpdateUserAdminUseCase
import ies.sequeros.dam.application.usecases.UpdateUserPhotoAdminUseCase
import ies.sequeros.dam.domain.repositories.IAnimalRepository
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.domain.repositories.IFavoritesRepository
import ies.sequeros.dam.domain.repositories.ILocalityRepository
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.RestAnimalRepository
import ies.sequeros.dam.infrastructure.RestAuthRepository
import ies.sequeros.dam.infrastructure.RestFavoritesRepository
import ies.sequeros.dam.infrastructure.RestLocalityRepository
import ies.sequeros.dam.infrastructure.RestShelterRepository
import ies.sequeros.dam.infrastructure.RestUserRepository
import ies.sequeros.dam.infrastructure.ktor.createHttpClient
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import ies.sequeros.dam.ui.admin.AdminUserEditViewModel
import ies.sequeros.dam.ui.admin.AdminUserProfileViewModel
import ies.sequeros.dam.ui.admin.AdminUsersViewModel
import ies.sequeros.dam.ui.animals.animalDetail.AnimalDetailViewModel
import ies.sequeros.dam.ui.animals.animalEdit.AnimalEditViewModel
import ies.sequeros.dam.ui.animals.misAnimales.MisAnimalesViewModel
import ies.sequeros.dam.ui.appsettings.AppSettings
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.appsettings.UserSessionManager
import ies.sequeros.dam.ui.inicio.InicioViewModel
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
    //val baseUrl = "http://localhost:8000"
    val baseUrl = "http://192.168.18.13:8000"

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
    single<IAnimalRepository> { RestAnimalRepository(get(), baseUrl) }
    single<IFavoritesRepository> { RestFavoritesRepository(get(), baseUrl) }

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
    factory { GetAnimalsUseCase(get()) }
    factory { GetAnimalByIdUseCase(get()) }
    factory { CreateAnimalUseCase(get()) }
    factory { UpdateAnimalUseCase(get()) }
    factory { DeleteAnimalUseCase(get()) }
    factory { UpdateAnimalPhotoUseCase(get()) }
    factory { GetUserByIdUseCase(get()) }
    factory { GetUsersUseCase(get()) }
    factory { UpdateUserAdminUseCase(get()) }
    factory { UpdateUserPhotoAdminUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { GetUserFavoritesUseCase(get()) }
    factory { AddFavoriteUseCase(get()) }
    factory { RemoveFavoriteUseCase(get()) }

    // --- Presentación ---
    // get() resuelve la instancia de Settings registrada por cada plataforma
    single { AppSettings(get()) }
    factory { AppViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get(), get()) }

    viewModel { ProtectorasViewModel(get(), get()) }
    viewModel { ShelterProfileViewModel(get()) }
    viewModel { ShelterEditViewModel(get(), get()) }
    viewModel { InicioViewModel(get()) }
    viewModel { AnimalDetailViewModel(get()) }
    viewModel { AnimalEditViewModel(get(), get(), get(), get(), get()) }
    viewModel { MisAnimalesViewModel(get()) }
    viewModel { AdminUsersViewModel(get()) }
    viewModel { AdminUserProfileViewModel(get(), get()) }
    viewModel { AdminUserEditViewModel(get(), get(), get()) }
}