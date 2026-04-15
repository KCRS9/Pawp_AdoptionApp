package ies.sequeros.dam.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.components.common.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PawpTopBar(
    onMenuClick: ()-> Unit,
    onNotificationClick: ()-> Unit,
    onAvatarClick: ()-> Unit,
    profileImage: String? = null
) {

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onMenuClick){

                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú"
                )
            }
        },
        actions = {
            // Cada acción es un elemento independiente — no mezclar dentro del mismo IconButton
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector        = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones"
                )
            }

            Spacer(Modifier.width(4.dp))

//            Image(
//                painter            = painterResource(Res.drawable.shelter_avatar),
//                contentDescription = "Mi Perfil",
//                modifier           = Modifier
//                    .padding(end = 12.dp)
//                    .size(36.dp)
//                    .clip(CircleShape)
//                    .clickable { onAvatarClick() }
//            )

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable{onAvatarClick()}
            ){
                UserAvatar(imageUrl = profileImage, size = 36.dp)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )

}