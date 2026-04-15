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
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.home.dialog.ThemeDialog
import ies.sequeros.dam.ui.inicio.InicioScreen
import ies.sequeros.dam.ui.mensajes.MensajesScreen
import ies.sequeros.dam.ui.profile.ProfileScreen

import ies.sequeros.dam.ui.protectoras.ProtectorasScreen
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
    CHANGE_EMAIL
}

@Composable
fun HomeScreen() {

    val appViewModel: AppViewModel = koinViewModel()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()
    val themeMode   by appViewModel.themeMode.collectAsStateWithLifecycle()

    var selectedTab     by remember { mutableStateOf(HomeTab.INICIO) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var homeDestination by remember { mutableStateOf(HomeDestination.TABS) }

    // DrawerState controla si el panel lateral está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // rememberCoroutineScope() proporciona el scope para lanzar corutinas en lambdas
    val scope = rememberCoroutineScope()

    // Diálogo de modo oscuro — se renderiza encima del drawer
    if (showThemeDialog) {
        ThemeDialog(
            currentMode    = themeMode,
            onModeSelected = { mode ->
                appViewModel.setThemeMode(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            PawpDrawer(
                currentUser = currentUser,
                onMyProfileClick = {
                    homeDestination = HomeDestination.PROFILE
                    scope.launch { drawerState.close()}
                                        },
                onMyAdoptionsClick = { scope.launch { drawerState.close() } },
                onMyAnimalsClick = { scope.launch { drawerState.close() } },
                onRegisterAnimalClick = { scope.launch { drawerState.close() } },
                onAdminPanelClick = { scope.launch { drawerState.close() } },
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
                    homeDestination = HomeDestination.CHANGE_PASSWORD
                    scope.launch { drawerState.close() }
                },
                onChangePasswordClick = {
                    homeDestination = HomeDestination.CHANGE_PASSWORD
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {

        when (homeDestination){

            HomeDestination.PROFILE -> {
                ProfileScreen(
                    onBack = {
                        homeDestination = HomeDestination.TABS
                    },
                    onEditClick ={
                        homeDestination = HomeDestination.EDIT_PROFILE
                    }

                )

            }

            HomeDestination.TABS -> {
                // ── Contenido principal ──────────────────────────────────────────────────
                Scaffold(
                    topBar = {
                        PawpTopBar(
                            onMenuClick         = { scope.launch { drawerState.open() } },
                            onNotificationClick = { },
                            onAvatarClick       = { homeDestination = HomeDestination.PROFILE},
                            profileImage = currentUser?.profileImage
                        )
                    },
                    bottomBar = {
                        PawpBottomNavigation(
                            selectedTab   = selectedTab,
                            onTabSelected = { selectedTab = it },
                            onAddClick    = { }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            HomeTab.INICIO -> InicioScreen()
                            HomeTab.SOCIAL      -> SocialScreen()
                            HomeTab.MENSAJES    -> MensajesScreen()
                            HomeTab.PROTECTORAS -> ProtectorasScreen()
                        }
                    }
                }
            }

            HomeDestination.EDIT_PROFILE -> {}

            HomeDestination.CHANGE_PASSWORD -> {}

            HomeDestination.CHANGE_EMAIL -> {}
        }


    }
}