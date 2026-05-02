package ies.sequeros.dam.di

import ies.sequeros.dam.application.usecases.AddFavoriteUseCase
import ies.sequeros.dam.application.usecases.CreateAdoptionUseCase
import ies.sequeros.dam.application.usecases.GetAdoptionDetailUseCase
import ies.sequeros.dam.application.usecases.GetMyAdoptionsUseCase
import ies.sequeros.dam.application.usecases.GetShelterAdoptionsUseCase
import ies.sequeros.dam.application.usecases.UpdateAdoptionStatusUseCase
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
import ies.sequeros.dam.application.usecases.CreatePostUseCase
import ies.sequeros.dam.application.usecases.GetPostsUseCase
import ies.sequeros.dam.application.usecases.GetShelterPostsUseCase
import ies.sequeros.dam.application.usecases.LikePostUseCase
import ies.sequeros.dam.application.usecases.GetPostByIdUseCase
import ies.sequeros.dam.application.usecases.DeletePostUseCase
import ies.sequeros.dam.application.usecases.GetCommentsUseCase
import ies.sequeros.dam.application.usecases.CreateCommentUseCase
import ies.sequeros.dam.application.usecases.DeleteCommentUseCase
import ies.sequeros.dam.domain.repositories.IAnimalRepository
import ies.sequeros.dam.domain.repositories.IPostRepository
import ies.sequeros.dam.domain.repositories.ICommentRepository
import ies.sequeros.dam.infrastructure.RestPostRepository
import ies.sequeros.dam.infrastructure.RestCommentRepository
import ies.sequeros.dam.ui.settings.deleteAccount.DeleteAccountViewModel
import ies.sequeros.dam.ui.social.PostFormViewModel
import ies.sequeros.dam.ui.social.SocialViewModel
import ies.sequeros.dam.ui.social.PostDetailViewModel
import ies.sequeros.dam.ui.social.UserPostsViewModel
import ies.sequeros.dam.ui.profile.ProfileViewModel
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.domain.repositories.IAdoptionRepository
import ies.sequeros.dam.domain.repositories.IFavoritesRepository
import ies.sequeros.dam.domain.repositories.ILocalityRepository
import ies.sequeros.dam.domain.repositories.IShelterRepository
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.RestAnimalRepository
import ies.sequeros.dam.infrastructure.RestAuthRepository
import ies.sequeros.dam.infrastructure.RestAdoptionRepository
import ies.sequeros.dam.infrastructure.RestFavoritesRepository
import ies.sequeros.dam.infrastructure.RestLocalityRepository
import ies.sequeros.dam.infrastructure.RestShelterRepository
import ies.sequeros.dam.infrastructure.RestUserRepository
import ies.sequeros.dam.infrastructure.ktor.createHttpClient
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import ies.sequeros.dam.ui.adoptions.AdoptionDetailViewModel
import ies.sequeros.dam.ui.adoptions.AdoptionFormViewModel
import ies.sequeros.dam.ui.adoptions.MisSolicitudesViewModel
import ies.sequeros.dam.ui.adoptions.SolicitudesProtectoraViewModel
import ies.sequeros.dam.ui.admin.AdminUserEditViewModel
import ies.sequeros.dam.ui.admin.UserProfileViewModel
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
    val baseUrl = "http://localhost:8000"
    //val baseUrl = "http://192.168.18.13:8000"

    // infraestructura
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
    single<IAdoptionRepository> { RestAdoptionRepository(get(), baseUrl) }
    single<IPostRepository> { RestPostRepository(get(), baseUrl) }
    single<ICommentRepository> { RestCommentRepository(get(), baseUrl) }

    // capa de aplicacion
    single { UserSessionManager(get()) }

    // casos de uso
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
    factory { CreateAdoptionUseCase(get()) }
    factory { GetMyAdoptionsUseCase(get()) }
    factory { GetShelterAdoptionsUseCase(get()) }
    factory { GetAdoptionDetailUseCase(get()) }
    factory { UpdateAdoptionStatusUseCase(get()) }
    factory { GetPostsUseCase(get()) }
    factory { GetShelterPostsUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { LikePostUseCase(get()) }
    factory { GetPostByIdUseCase(get()) }
    factory { DeletePostUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { CreateCommentUseCase(get()) }
    factory { DeleteCommentUseCase(get()) }

    // presentacion
    // get() resuelve la instancia de Settings registrada por cada plataforma
    single { AppSettings(get()) }
    factory { AppViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get(), get()) }

    viewModel { ProtectorasViewModel(get(), get()) }
    viewModel { ShelterProfileViewModel(get(), get()) }
    viewModel { ShelterEditViewModel(get(), get()) }
    viewModel { InicioViewModel(get()) }
    viewModel { AnimalDetailViewModel(get()) }
    viewModel { AnimalEditViewModel(get(), get(), get(), get(), get()) }
    viewModel { MisAnimalesViewModel(get()) }
    viewModel { AdminUsersViewModel(get()) }
    viewModel { UserProfileViewModel(get(), get()) }
    viewModel { AdminUserEditViewModel(get(), get(), get()) }
    viewModel { AdoptionFormViewModel(get()) }
    viewModel { MisSolicitudesViewModel(get()) }
    viewModel { SolicitudesProtectoraViewModel(get()) }
    viewModel { AdoptionDetailViewModel(get(), get()) }
    viewModel { SocialViewModel(get(), get()) }
    viewModel { PostFormViewModel(get(), get()) }
    viewModel { PostDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { UserPostsViewModel(get(), get()) }
    viewModel { DeleteAccountViewModel() }
}
