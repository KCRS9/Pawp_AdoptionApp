package ies.sequeros.dam.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ies.sequeros.dam.ui.inicio.InicioScreen
import ies.sequeros.dam.ui.mensajes.MensajesScreen
import ies.sequeros.dam.ui.protectoras.ProtectorasScreen
import ies.sequeros.dam.ui.social.SocialScreen

enum class HomeTab {
    INICIO,
    SOCIAL,
    MENSAJES,
    PROTECTORAS
}


@Composable
fun HomeScreen () {

    var selectedTab by remember { mutableStateOf(HomeTab.INICIO) }

    Scaffold (
        topBar = {
            PawpTopBar(
                onMenuClick = {},
                onNotificationClick = {},
                onAvatarClick = {}
            )
        },
        bottomBar = {
            PawpBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it},
                onAddClick = {}
            )
        }
    ){
        innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            when (selectedTab){
                HomeTab.INICIO -> InicioScreen()
                HomeTab.SOCIAL -> SocialScreen()
                HomeTab.MENSAJES -> MensajesScreen()
                HomeTab.PROTECTORAS -> ProtectorasScreen()
            }
        }
    }



}