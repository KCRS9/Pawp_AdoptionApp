package ies.sequeros.dam.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.adoptions.AdoptionDetailScreen
import ies.sequeros.dam.ui.adoptions.AdoptionFormScreen
import ies.sequeros.dam.ui.adoptions.MisSolicitudesScreen
import ies.sequeros.dam.ui.adoptions.SolicitudesProtectoraScreen
import ies.sequeros.dam.ui.admin.AdminPanelScreen
import ies.sequeros.dam.ui.admin.AdminUserEditScreen
import ies.sequeros.dam.ui.admin.AdminUserProfileScreen
import ies.sequeros.dam.ui.admin.AdminUsersScreen
import ies.sequeros.dam.ui.animals.animalDetail.AnimalDetailScreen
import ies.sequeros.dam.ui.animals.animalEdit.AnimalEditScreen
import ies.sequeros.dam.ui.animals.misAnimales.MisAnimalesScreen
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.home.dialog.ThemeDialog
import ies.sequeros.dam.ui.inicio.InicioScreen
import ies.sequeros.dam.ui.mensajes.MensajesScreen
import ies.sequeros.dam.ui.profile.EditProfileScreen
import ies.sequeros.dam.ui.profile.ProfileScreen
import ies.sequeros.dam.ui.shelters.ProtectorasScreen
import ies.sequeros.dam.ui.settings.changeEmail.ChangeEmailScreen
import ies.sequeros.dam.ui.settings.changePassword.ChangePasswordScreen
import ies.sequeros.dam.ui.shelters.shelterEdit.ShelterEditScreen
import ies.sequeros.dam.ui.shelters.shelterProfile.ShelterProfileScreen
import ies.sequeros.dam.ui.social.SocialScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

enum class HomeTab {
    INICIO,
    SOCIAL,
    MENSAJES,
    PROTECTORAS
}

enum class HomeDestination {
    TABS,
    PROFILE,
    EDIT_PROFILE,
    CHANGE_PASSWORD,
    CHANGE_EMAIL,
    SHELTER_PROFILE,
    SHELTER_EDIT,
    ANIMAL_DETAIL,
    ANIMAL_EDIT,
    MIS_ANIMALES,
    ADMIN_PANEL,
    ADMIN_USERS,
    ADMIN_USER_PROFILE,
    ADMIN_USER_EDIT,
    ADOPTION_FORM,
    MIS_SOLICITUDES,
    SHELTER_ADOPTIONS,
    ADOPTION_DETAIL,
}

@Composable
fun HomeScreen() {

    val appViewModel: AppViewModel = koinViewModel()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()
    val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(HomeTab.INICIO) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var homeDestination by remember { mutableStateOf(HomeDestination.TABS) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedShelterId by remember { mutableStateOf<String?>(null) }
    var selectedAnimalId by remember { mutableStateOf<String?>(null) }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var selectedAdoptionId by remember { mutableStateOf<Int?>(null) }
    var adoptionAnimalId by remember { mutableStateOf("") }
    var adoptionAnimalName by remember { mutableStateOf("") }
    var adoptionIsShelter by remember { mutableStateOf(false) }
    var animalDetailBackDest by remember { mutableStateOf(HomeDestination.TABS) }
    var userProfileBackDest by remember { mutableStateOf(HomeDestination.ADMIN_USERS) }
    var misAnimalesBackDest by remember { mutableStateOf(HomeDestination.TABS) }

    if (showThemeDialog) {
        ThemeDialog(
            currentMode = themeMode,
            onModeSelected = { mode ->
                appViewModel.setThemeMode(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PawpDrawer(
                currentUser = currentUser,
                onMyProfileClick = {
                    homeDestination = HomeDestination.PROFILE
                    scope.launch { drawerState.close() }
                },
                onMyAdoptionsClick = {
                    homeDestination = HomeDestination.MIS_SOLICITUDES
                    scope.launch { drawerState.close() }
                },

                onMyAnimalsClick = {
                    selectedShelterId = currentUser?.shelterId
                    misAnimalesBackDest = HomeDestination.TABS
                    homeDestination = HomeDestination.MIS_ANIMALES
                    scope.launch { drawerState.close() }
                },

                onRegisterAnimalClick = {
                    selectedAnimalId = null
                    homeDestination = HomeDestination.ANIMAL_EDIT
                    scope.launch { drawerState.close() }
                },

                onShelterAdoptionsClick = {
                    homeDestination = HomeDestination.SHELTER_ADOPTIONS
                    scope.launch { drawerState.close() }
                },

                onAdminPanelClick = {
                    homeDestination = HomeDestination.ADMIN_PANEL
                    scope.launch { drawerState.close() }
                },

                onNotificationsClick = { scope.launch { drawerState.close() } },
                onThemeClick = {
                    showThemeDialog = true
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    appViewModel.logout()
                },
                onChangeEmailClick = {
                    homeDestination = HomeDestination.CHANGE_EMAIL
                    scope.launch { drawerState.close() }
                },
                onChangePasswordClick = {
                    homeDestination = HomeDestination.CHANGE_PASSWORD
                    scope.launch { drawerState.close() }
                },
                onMyShelterClick = {
                    selectedShelterId = currentUser?.shelterId
                    homeDestination = HomeDestination.SHELTER_PROFILE
                    scope.launch { drawerState.close() }
                },
            )
        }
    ) {

        when (homeDestination) {

            HomeDestination.PROFILE -> {
                ProfileScreen(
                    onBack = { homeDestination = HomeDestination.TABS },
                    onEditClick = { homeDestination = HomeDestination.EDIT_PROFILE },
                    onAnimalClick = { id ->
                        selectedAnimalId = id
                        animalDetailBackDest = HomeDestination.PROFILE
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    }
                )
            }

            HomeDestination.TABS -> {
                Scaffold(
                    topBar = {
                        PawpTopBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onNotificationClick = { },
                            onAvatarClick = { homeDestination = HomeDestination.PROFILE },
                            profileImage = currentUser?.profileImage
                        )
                    },
                    bottomBar = {
                        PawpBottomNavigation(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            onAddClick = { }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            HomeTab.INICIO -> InicioScreen(
                                onAnimalClick = { id ->
                                    selectedAnimalId = id
                                    animalDetailBackDest = HomeDestination.TABS
                                    homeDestination = HomeDestination.ANIMAL_DETAIL
                                }
                            )
                            HomeTab.SOCIAL -> SocialScreen()
                            HomeTab.MENSAJES -> MensajesScreen()
                            HomeTab.PROTECTORAS -> ProtectorasScreen(
                                onShelterClick = { shelterId ->
                                    selectedShelterId = shelterId
                                    homeDestination = HomeDestination.SHELTER_PROFILE
                                }
                            )
                        }
                    }
                }
            }

            HomeDestination.EDIT_PROFILE -> {
                EditProfileScreen(onBack = { homeDestination = HomeDestination.PROFILE })
            }

            HomeDestination.CHANGE_PASSWORD -> {
                ChangePasswordScreen(onBack = { homeDestination = HomeDestination.TABS })
            }

            HomeDestination.CHANGE_EMAIL -> {
                ChangeEmailScreen(onBack = { homeDestination = HomeDestination.TABS })
            }

            HomeDestination.SHELTER_PROFILE -> {
                val isOwnShelter = currentUser?.role == "admin" ||
                    (currentUser?.shelterId != null && currentUser?.shelterId == selectedShelterId)

                ShelterProfileScreen(

                    shelterId = selectedShelterId ?: "",
                    onBack = { homeDestination = HomeDestination.TABS },
                    onEditClick = if (isOwnShelter) { { homeDestination = HomeDestination.SHELTER_EDIT } } else null,
                    onAdminClick = if (currentUser?.role == "admin") {
                        { adminId ->
                            selectedUserId = adminId
                            userProfileBackDest = HomeDestination.SHELTER_PROFILE
                            homeDestination = HomeDestination.ADMIN_USER_PROFILE
                        }
                    } else null,
                    onAnimalClick = { id ->
                        selectedAnimalId = id
                        animalDetailBackDest = HomeDestination.SHELTER_PROFILE
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    },
                    onVerAnimalesClick = {
                        misAnimalesBackDest = HomeDestination.SHELTER_PROFILE
                        homeDestination = HomeDestination.MIS_ANIMALES
                    }
                )
            }

            HomeDestination.SHELTER_EDIT -> {
                ShelterEditScreen(
                    shelterId = selectedShelterId ?: "",
                    onBack = { homeDestination = HomeDestination.SHELTER_PROFILE }
                )
            }

            HomeDestination.ANIMAL_DETAIL -> {
                val isUser = currentUser?.role == "user"
                val isAdmin = currentUser?.role == "admin"
                AnimalDetailScreen(
                    animalId = selectedAnimalId ?: "",
                    onBack = { homeDestination = animalDetailBackDest },
                    onShelterClick = { shelterId ->
                        selectedShelterId = shelterId
                        homeDestination = HomeDestination.SHELTER_PROFILE
                    },
                    currentUserShelterId = currentUser?.shelterId,
                    isSystemAdmin = isAdmin,
                    onEditClick = if (isAdmin || currentUser?.shelterId != null) { { homeDestination = HomeDestination.ANIMAL_EDIT } } else null,
                    onAdoptClick = if (isUser) { animalId, animalName ->
                        adoptionAnimalId = animalId
                        adoptionAnimalName = animalName
                        homeDestination = HomeDestination.ADOPTION_FORM
                    } else null
                )
            }

            HomeDestination.ANIMAL_EDIT -> {
                AnimalEditScreen(
                    animalId = selectedAnimalId,
                    onBack = {
                        if (selectedAnimalId != null) homeDestination = HomeDestination.ANIMAL_DETAIL
                        else homeDestination = HomeDestination.TABS
                    },
                    onSaved = { newId ->
                        selectedAnimalId = newId
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    },
                    onDeleted = { homeDestination = HomeDestination.SHELTER_PROFILE }
                )
            }

            HomeDestination.MIS_ANIMALES -> {
                MisAnimalesScreen(
                    shelterId = selectedShelterId ?: "",
                    onAnimalClick = { id ->
                        selectedAnimalId = id
                        animalDetailBackDest = HomeDestination.MIS_ANIMALES
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    },
                    onBack = { homeDestination = misAnimalesBackDest }
                )
            }

            HomeDestination.ADMIN_PANEL -> {
                AdminPanelScreen(
                    onBack = { homeDestination = HomeDestination.TABS },
                    onSheltersClick = {
                        selectedTab = HomeTab.PROTECTORAS
                        homeDestination = HomeDestination.TABS
                    },
                    onUsersClick = { homeDestination = HomeDestination.ADMIN_USERS }
                )
            }

            HomeDestination.ADMIN_USERS -> {
                AdminUsersScreen(
                    onBack = { homeDestination = HomeDestination.ADMIN_PANEL },
                    onUserClick = { userId ->
                        selectedUserId = userId
                        userProfileBackDest = HomeDestination.ADMIN_USERS
                        homeDestination = HomeDestination.ADMIN_USER_PROFILE
                    }
                )
            }

            HomeDestination.ADMIN_USER_PROFILE -> {
                AdminUserProfileScreen(
                    userId = selectedUserId ?: "",
                    onBack = { homeDestination = userProfileBackDest },
                    onEditClick = { homeDestination = HomeDestination.ADMIN_USER_EDIT },
                    onAnimalClick = { id ->
                        selectedAnimalId = id
                        animalDetailBackDest = HomeDestination.ADMIN_USER_PROFILE
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    }
                )
            }

            HomeDestination.ADMIN_USER_EDIT -> {
                AdminUserEditScreen(
                    userId = selectedUserId ?: "",
                    onBack = { homeDestination = HomeDestination.ADMIN_USER_PROFILE }
                )
            }

            HomeDestination.ADOPTION_FORM -> {
                AdoptionFormScreen(
                    animalId = adoptionAnimalId,
                    animalName = adoptionAnimalName,
                    userEmail = currentUser?.email ?: "",
                    onBack = { homeDestination = HomeDestination.ANIMAL_DETAIL }
                )
            }

            HomeDestination.MIS_SOLICITUDES -> {
                MisSolicitudesScreen(
                    onBack = { homeDestination = HomeDestination.TABS },
                    onAdoptionClick = { id ->
                        selectedAdoptionId = id
                        adoptionIsShelter = false
                        homeDestination = HomeDestination.ADOPTION_DETAIL
                    }
                )
            }

            HomeDestination.SHELTER_ADOPTIONS -> {
                SolicitudesProtectoraScreen(
                    onBack = { homeDestination = HomeDestination.TABS },
                    onAdoptionClick = { id ->
                        selectedAdoptionId = id
                        adoptionIsShelter = true
                        homeDestination = HomeDestination.ADOPTION_DETAIL
                    }
                )
            }

            HomeDestination.ADOPTION_DETAIL -> {
                AdoptionDetailScreen(
                    adoptionId = selectedAdoptionId ?: 0,
                    isShelter = adoptionIsShelter,
                    onBack = {
                        homeDestination = if (adoptionIsShelter) HomeDestination.SHELTER_ADOPTIONS
                                          else HomeDestination.MIS_SOLICITUDES
                    },
                    onAnimalClick = { id ->
                        selectedAnimalId = id
                        animalDetailBackDest = HomeDestination.ADOPTION_DETAIL
                        homeDestination = HomeDestination.ANIMAL_DETAIL
                    },
                    onUserClick = { id ->
                        selectedUserId = id
                        userProfileBackDest = HomeDestination.ADOPTION_DETAIL
                        homeDestination = HomeDestination.ADMIN_USER_PROFILE
                    }
                )
            }

        }
    }
}
