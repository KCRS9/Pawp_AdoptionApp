package ies.sequeros.dam.ui.social

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ies.sequeros.dam.domain.models.Post
import ies.sequeros.dam.domain.models.toDisplayDate
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpSurfaceDark

private val CardShape = RoundedCornerShape(12.dp)
private val PhotoShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onUserClick: ((String) -> Unit)? = null,
    onAnimalClick: ((String) -> Unit)? = null,
    onPostClick: (() -> Unit)? = null,
    showCommentBar: Boolean = true,
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
    ) {
        Column {

            AsyncImage(
                model = post.photoUrl,
                contentDescription = "Foto de la publicación",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .clip(PhotoShape)
                    .then(if (onPostClick != null) Modifier.clickable { onPostClick() } else Modifier)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // bloque izquierdo: avatar + nombre + fecha
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (onUserClick != null)
                                Modifier.clickable { onUserClick(post.userId) }
                            else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = post.userImage,
                        contentDescription = post.userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Column {
                        Text(
                            text = post.userName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = post.createdAt.toDisplayDate(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // bloque derecho: chip animal (opcional) + contadores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // chip del animal etiquetado
                    if (post.animalId != null && post.animalName != null) {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = PawpPurple.copy(alpha = 0.12f),
                            modifier = Modifier
                                .widthIn(max = 90.dp)
                                .then(
                                    if (onAnimalClick != null)
                                        Modifier.clickable { onAnimalClick(post.animalId) }
                                    else Modifier
                                )
                        ) {
                            Text(
                                text = "🐾 ${post.animalName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PawpPurple,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // contador de comentarios
                    Text(
                        text = "💬 ${post.comments}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = if (onPostClick != null) Modifier.clickable { onPostClick() } else Modifier
                    )

                    // like toggle + contador
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = if (post.likedByMe) Icons.Default.Favorite
                                          else Icons.Default.FavoriteBorder,
                            contentDescription = if (post.likedByMe) "Quitar like" else "Dar like",
                            tint = if (post.likedByMe) Color.Red
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onLikeClick() }
                        )
                        Text(
                            text = "${post.likes}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!post.text.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = post.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // barra de comentario (oculta en postdetailscreen)
            if (showCommentBar) {
                HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Añadir un comentario...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .weight(1f)
                            .then(if (onPostClick != null) Modifier.clickable { onPostClick() } else Modifier)
                    )
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar comentario",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
