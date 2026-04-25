package ies.sequeros.dam.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleLight
import ies.sequeros.dam.ui.theme.PawpSurfaceDark
import org.jetbrains.compose.resources.painterResource
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.icon_cat
import pawp_adoption.composeapp.generated.resources.icon_dog
import pawp_adoption.composeapp.generated.resources.icon_hamster
import pawp_adoption.composeapp.generated.resources.icon_rabbit
import pawp_adoption.composeapp.generated.resources.icon_turtle

private val CardShape = RoundedCornerShape(12.dp)
private val PhotoShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)

@Composable
fun AnimalMiniCard(

    name: String,
    species: String,
    gender: String = "unknown",
    locationName: String? = null,
    profileImage: String? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) PawpSurfaceDark else Color.White

    Surface(

        shape = CardShape,
        color = cardBg,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = CardShape)
            .clickable(onClick = onClick)
    ) {
        Column {
            //Foto con overlay de favorito
            Box {
                if (!profileImage.isNullOrBlank()) {

                    AsyncImage(

                        model = profileImage,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(PhotoShape)
                    )
                } else {

                    val iconRes = when (species.lowercase()) {

                        "perro" -> Res.drawable.icon_dog
                        "gato" -> Res.drawable.icon_cat
                        "conejo" -> Res.drawable.icon_rabbit
                        "reptil" -> Res.drawable.icon_turtle
                        else -> Res.drawable.icon_hamster
                    }

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(PhotoShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(

                            painter = painterResource(iconRes),
                            contentDescription = species,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .then(if (onFavoriteClick != null) Modifier.clickable { onFavoriteClick() } else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isFavorite) Color.Red else PawpPurple,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Info ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color.Black,
                        maxLines = 1
                    )
                    if (!locationName.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PawpPurple,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = locationName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Icono de género
                val (genderSymbol, genderColor) = when (gender) {
                    "male" -> "♂" to Color(0xFF4A90D9)
                    "female" -> "♀" to Color(0xFFD94A8B)
                    else -> "?" to (if (isDark) PawpPurpleLight else PawpPurple)
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(genderColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = genderSymbol,
                        style = MaterialTheme.typography.labelSmall,
                        color = genderColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}
