package ies.sequeros.dam.ui.components.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ies.sequeros.dam.ui.theme.PawpPurple


@Composable
fun UserAvatar(

    imageUrl: String?,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {

    if(imageUrl != null){

        AsyncImage(
            model =imageUrl,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    }else{

        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Foto de perfil",
            tint = PawpPurple,
            modifier = modifier.size(size)
        )
    }
}