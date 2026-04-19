package ies.sequeros.dam.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import ies.sequeros.dam.ui.theme.PawpPurpleDark

@Composable
fun AvatarWithPencil(
    imageUrl: String?,
    previewBytes: ByteArray? = null,
    size: Dp = 96.dp,
    isUploading: Boolean = false,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null   // null = no se muestra el lápiz
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {

        if (isUploading) {
            Box(Modifier.size(size), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(size * 0.5f))
            }
        } else if (previewBytes != null) {
            // Vista previa local sin caché — muestra la imagen seleccionada antes de confirmar
            val context = LocalPlatformContext.current
            val request = ImageRequest.Builder(context)
                .data(previewBytes)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
            AsyncImage(
                model              = request,
                contentDescription = "Vista previa",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.size(size).clip(CircleShape)
            )
        } else {
            UserAvatar(imageUrl = imageUrl, size = size)
        }

        if (onEditClick != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PawpPurpleDark)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Edit,
                    contentDescription = "Editar foto",
                    tint               = Color.White,
                    modifier           = Modifier.size(16.dp)
                )
            }
        }
    }
}
