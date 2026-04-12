package ies.sequeros.dam.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.theme.PawpNavInactive
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import ies.sequeros.dam.ui.theme.PawpPurpleLight

@Composable
fun PawpBottomNavigation(

   selectedTab: HomeTab,
   onTabSelected: (HomeTab) -> Unit,
   onAddClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
    ){
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            //containerColor = Color.Transparent,
            tonalElevation = 4.dp
        ) {

            val itemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = PawpPurpleDark,
                selectedTextColor = PawpPurpleDark,
                unselectedIconColor = PawpNavInactive,
                unselectedTextColor = PawpNavInactive,
                indicatorColor = PawpPurple.copy(alpha = 0.12f)
            )

            //Tab: Inicio
            PawpNavItem(
                tab =HomeTab.INICIO,
                selectedTab = selectedTab,
                label = "Inicio",
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                colors = itemColors,
                onTabSelected = onTabSelected
            )

            //Tab: Social
            PawpNavItem(
                tab =HomeTab.SOCIAL,
                selectedTab = selectedTab,
                label = "Social",
                iconSelected = Icons.Filled.Groups,
                iconUnselected = Icons.Outlined.Groups,
                colors = itemColors,
                onTabSelected = onTabSelected
            )

            //Espacio en blanco para poner el boton de agregar Post
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Spacer(Modifier.size(32.dp)) },
                label = {Spacer(Modifier.height(12.dp))},
                enabled = false,
                colors = NavigationBarItemDefaults.colors(
                    disabledIconColor = Color.Transparent,
                    disabledTextColor = Color.Transparent
                )
            )

            // Tab: Mensajes

            PawpNavItem(
                tab =HomeTab.MENSAJES,
                selectedTab = selectedTab,
                label = "Mensajes",
                iconSelected = Icons.Filled.Chat,
                iconUnselected = Icons.Outlined.Chat,
                colors = itemColors,
                onTabSelected = onTabSelected
            )

            // Tab: Protectoras
            PawpNavItem(
                tab =HomeTab.PROTECTORAS,
                selectedTab = selectedTab,
                label = "Protectoras",
                iconSelected = Icons.Filled.Pets,
                iconUnselected = Icons.Outlined.Pets,
                colors = itemColors,
                onTabSelected = onTabSelected
            )


            /*
            PawpNavItem(
                tab =HomeTab.,
                selectedTab = selectedTab,
                label = "",
                iconSelected = Icons.Filled.,
                iconUnselected = Icons.Outlined.,
                colors = itemColors,
                onTabSelected = onTabSelected
            )
             */


        }

        FloatingActionButton(
            onClick          = onAddClick,
            modifier         = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-5).dp)
                .size(56.dp),
            shape            = CircleShape,
            containerColor   = PawpPurpleDark,
            contentColor     = Color.White,
            elevation        = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp
            )
        ) {
            Icon(
                imageVector        = Icons.Default.Camera,
                contentDescription = "Nueva publicación"
            )
        }
    }
}

@Composable
private fun RowScope.PawpNavItem(
    tab: HomeTab,
    selectedTab: HomeTab,
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    colors: NavigationBarItemColors,
    onTabSelected: (HomeTab) -> Unit
){

    val isSelected = tab == selectedTab

    NavigationBarItem(
        selected = isSelected,
        onClick = { onTabSelected(tab)},
        icon = {
            Icon(
                imageVector = if (isSelected) iconSelected else iconUnselected,
                contentDescription = label
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = colors
    )
}