package ies.sequeros.dam.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.ui.components.common.AnimalMiniCard
import ies.sequeros.dam.ui.extensions.toRoleLabel
import ies.sequeros.dam.ui.extensions.toTitleCase
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpSurfaceDark

@Composable
fun ProfileContent(
    user: User,
    isOwnProfile: Boolean = true,
    onEditClick: () -> Unit = {},
    favoriteAnimals: List<AnimalSummary> = emptyList(),
    onAnimalClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .widthIn(max = 480.dp)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Foto con lápiz
        Box(contentAlignment = Alignment.Center) {

            Box(

                modifier = Modifier.size(196.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(

                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(196.dp)
                )
                if (user.profileImage != null) {

                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(196.dp).clip(CircleShape)
                    )
                }
            }

            if (isOwnProfile) {

                Box(

                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PawpPurple)
                        .clickable(onClick = onEditClick),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(

                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        //Nombre
        Text(

            text = user.name.toTitleCase(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // Chips rol + provincia
        Row(

            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            Surface(

                shape = MaterialTheme.shapes.extraLarge,
                color = PawpPurple.copy(alpha = 0.12f)
            ) {
                Text(

                    text = user.role.toRoleLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    color = PawpPurple,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            user.locationName?.let { province ->

                Surface(

                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.52f)
                ) {
                    Text(

                        text     = province,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        //Estadísticas
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatColumn(label = "Publicaciones", value = "0", onClick = {})
            ProfileStatColumn(label = "Favoritos",     value = favoriteAnimals.size.toString(), onClick = {})
            ProfileStatColumn(label = "Seguidores",    value = "0", onClick = {})
        }

        Spacer(Modifier.height(16.dp))

        //Descripción
        val descriptionBg = if (isSystemInDarkTheme()) PawpSurfaceDark else Color(0xFFF0F0F0)

        Surface(

            shape    = MaterialTheme.shapes.medium,
            color    = descriptionBg,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (!user.description.isNullOrBlank()) {

                Text(
                    text     = user.description,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {

                Text(
                    text      = "Cuéntanos algo de ti y qué es lo que más te gusta de los animales...",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontStyle = FontStyle.Italic,
                    modifier  = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        //Favoritos
        ProfileSection(title = "Favoritos") {
            if (favoriteAnimals.isEmpty()) {
                Text(
                    text = if (isOwnProfile) "Aún no tienes ningún animal favorito"
                           else "Este usuario aún no tiene ningún animal favorito",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favoriteAnimals, key = { it.id }) { animal ->
                        AnimalMiniCard(
                            name = animal.name,
                            species = animal.species,
                            gender = animal.gender,
                            locationName = animal.locationName,
                            profileImage = animal.profileImage,
                            onClick = { onAnimalClick(animal.id) },
                            modifier = Modifier.widthIn(max = 200.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        //Publicaciones
        ProfileSection(title = "Publicaciones") {

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                repeat(3) { PublicationCardPlaceholder() }
            }
        }
    }
}

//Helpers internos

@Composable
internal fun ProfileStatColumn(label: String, value: String, onClick: () -> Unit) {

    Column(

        modifier            = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
internal fun AnimalCardPlaceholder() {

    Surface(
        shape    = RoundedCornerShape(12.dp),
        color    = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(width = 120.dp, height = 140.dp)
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            ) {
                Text(
                    text     = "Animal",
                    style    = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
internal fun PublicationCardPlaceholder() {

    Surface(

        shape    = RoundedCornerShape(12.dp),
        color    = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Box(

            modifier         = Modifier.padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(

                text  = "Publicación",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}