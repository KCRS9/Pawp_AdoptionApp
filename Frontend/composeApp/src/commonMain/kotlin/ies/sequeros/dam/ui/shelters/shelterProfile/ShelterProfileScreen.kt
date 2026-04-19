package ies.sequeros.dam.ui.shelters.shelterProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AnimalMiniCard
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.profile.ProfileStatColumn
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.pawpOnSurfaceTextColor
import ies.sequeros.dam.ui.theme.pawpSurfaceColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShelterProfileScreen(
    shelterId: String,
    onBack: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onAdminClick: (String) -> Unit = {},
    onAnimalClick: (String) -> Unit = {},
    onVerAnimalesClick: (() -> Unit)? = null
) {
    val viewModel: ShelterProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(shelterId) { viewModel.load(shelterId) }

    SettingsFormScaffold(
        title = state.shelter?.name ?: "Protectora",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@SettingsFormScaffold
        }

        val shelter = state.shelter ?: return@SettingsFormScaffold

        AvatarWithPencil(
            imageUrl = shelter.profileImage,
            size = 196.dp,
            onEditClick = onEditClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = shelter.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shelter.locationName?.let { location ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.52f)
                ) {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.clickable { onAdminClick(shelter.adminId) }
            ) {
                Text(
                    text = "Admin: ${shelter.adminName ?: shelter.adminId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatColumn(label = "En adopción", value = shelter.animals.size.toString(), onClick = {})
            ProfileStatColumn(label = "Publicaciones", value = "0", onClick = {})
            ProfileStatColumn(label = "Seguidores", value = "0", onClick = {})
        }

        Spacer(Modifier.height(16.dp))

        val descriptionBg = pawpSurfaceColor()
        val descriptionText = pawpOnSurfaceTextColor()
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = descriptionBg,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (shelter.description.isNotBlank()) {
                Text(
                    text = shelter.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = descriptionText,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text = "Esta protectora aún no tiene descripción.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = descriptionText.copy(alpha = 0.5f),
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        ShelterInfoRow("Teléfono", shelter.phone)
        ShelterInfoRow("Email", shelter.email)
        shelter.address?.let { ShelterInfoRow("Dirección", it) }
        shelter.website?.let { ShelterInfoRow("Web", it) }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // ── Sección animales en adopción
        if (onVerAnimalesClick != null) {
            TextButton(onClick = onVerAnimalesClick) {
                Text("En adopción (${shelter.animals.size})", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Text(
                text = "En adopción (${shelter.animals.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        if (shelter.animals.isEmpty()) {
            Text(
                text = "Esta protectora aún no tiene animales registrados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(shelter.animals) { animal ->
                    AnimalMiniCard(
                        name = animal.name,
                        species = animal.species,
                        gender = animal.gender,
                        profileImage = animal.profileImage,
                        modifier = Modifier.width(160.dp),
                        onClick = { onAnimalClick(animal.id) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text("Publicaciones", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Box(Modifier.padding(12.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = "Próximamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ShelterInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}
