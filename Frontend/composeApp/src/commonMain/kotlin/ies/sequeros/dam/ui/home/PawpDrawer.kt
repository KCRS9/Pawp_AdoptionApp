package ies.sequeros.dam.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.star
import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import org.jetbrains.compose.resources.painterResource
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.shelter_avatar

@Composable
fun PawpDrawer(

    currentUser: User?,
    onMyProfileClick: () -> Unit,
    onMyAdoptionsClick: () -> Unit,
    onMyAnimalsClick: () -> Unit,
    onRegisterAnimalClick: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onNotificationsClick:() -> Unit,
    onThemeClick: () -> Unit,
    onLogoutClick:() -> Unit
) {

    var settingsExpanded by remember { mutableStateOf(false) }
    val horizontalDivider = Modifier.padding(vertical = 8.dp)

    ModalDrawerSheet {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {

            //Cabecera
            DrawerHeader(currentUser = currentUser)
            HorizontalDivider(horizontalDivider)

            //Sección General

            DrawerItem(
                icon = Icons.Filled.AccountCircle,
                label = "Mi perfil",
                onClick = onMyProfileClick
            )

            DrawerItem(
                icon = Icons.Filled.Favorite,
                label = "Mis Solicitudes",
                onClick = onMyAdoptionsClick
            )

            // PROTECTORAS
            if(currentUser?.role == "shelter"){

                HorizontalDivider(horizontalDivider)
                DrawerSectionLabel(text = "Protectora")

                DrawerItem(
                    icon = Icons.Filled.Pets,
                    label = "Mis animales",
                    onClick = onMyAnimalsClick
                )

                DrawerItem(
                    icon    = Icons.Filled.AddBox,
                    label   = "Registrar animal",
                    onClick = onRegisterAnimalClick
                )
            }
            //ADMINISTRADORS
            if (currentUser?.role == "admin") {

                HorizontalDivider(horizontalDivider)
                DrawerSectionLabel(text = "Administración")

                DrawerItem(
                    icon    = Icons.Filled.AdminPanelSettings,
                    label   = "Panel de administración",
                    onClick = onAdminPanelClick
                )
            }

            HorizontalDivider(horizontalDivider)

            //AJUSTES
            NavigationDrawerItem(
                icon = {Icon(Icons.Filled.Settings, contentDescription = "Ajustes")},
                label = {Text("Ajustes")},
                selected = false,
                badge = {
                    Icon(imageVector = if (settingsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                },
                onClick = { settingsExpanded = !settingsExpanded},
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            if (settingsExpanded) {

                DrawerSubItem(
                    icon    = Icons.Filled.DarkMode,
                    label   = "Modo oscuro",
                    onClick = onThemeClick
                )

                DrawerSubItem(
                    icon    = Icons.Filled.Notifications,
                    label   = "Notificaciones",
                    onClick = onNotificationsClick
                )
            }

            HorizontalDivider(horizontalDivider)

            //Cerrar Sesión
            DrawerItem(
                icon = Icons.Filled.Logout,
                label = "Cerrar sesión",
                onClick = onLogoutClick,
                tint = MaterialTheme.colorScheme.error
            )





        }
    }
}



@Composable
private fun DrawerHeader(currentUser: User?){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PawpPurpleDark)
            .padding(24.dp)
    ){

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(Res.drawable.shelter_avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(8.dp))

            Column {

                Text(
                    text = currentUser?.name?: "Cargando...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun DrawerSectionLabel(text: String){
    Text(

        text = text.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: ()-> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
){
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription=label, tint=tint) },
        label = {Text(label, color = tint)},
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

// Subítem con sangría adicional para indicar jerarquía dentro de Ajustes
@Composable
private fun DrawerSubItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
){
    NavigationDrawerItem(
        icon     = { Icon(icon, contentDescription = label) },
        label    = { Text(label) },
        selected  = false,
        onClick   = onClick,
        modifier  = Modifier.padding(
            start = 28.dp,
            end = 12.dp,
            top = 0.dp,
            bottom = 0.dp
        )
    )
}