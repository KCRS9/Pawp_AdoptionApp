package ies.sequeros.dam.ui.shelters.shelterProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.profile.ProfileStatColumn
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpSurfaceDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShelterProfileScreen(
    shelterId: String,
    onBack: () -> Unit,
    // null = vista de solo lectura (listado público)
    // non-null = el usuario es admin de esta protectora
    onEditClick: (() -> Unit)? = null,
    onAdminClick: (String) -> Unit = {}
) {
    val viewModel: ShelterProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(shelterId) { viewModel.load(shelterId) }

    SettingsFormScaffold(
        title        = state.shelter?.name ?: "Protectora",
        onBack       = onBack,
        snackbarHost = snackbarHost
    ) {

        if (state.isLoading) {
            Box(
                modifier         = Modifier.fillMaxWidth().padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@SettingsFormScaffold
        }

        val shelter = state.shelter ?: return@SettingsFormScaffold

        // ── Foto de perfil con lápiz opcional (solo para el admin)
        AvatarWithPencil(
            imageUrl    = shelter.profileImage,
            size        = 196.dp,
            onEditClick = onEditClick,
            modifier    = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        // ── Nombre
        Text(
            text      = shelter.name,
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // ── Píldora de ubicación + píldora de admin en fila centrada bajo el nombre
        Row(
            modifier              = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shelter.locationName?.let { location ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.52f)
                ) {
                    Text(
                        text     = location,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Surface(
                shape    = MaterialTheme.shapes.extraLarge,
                color    = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.clickable { onAdminClick(shelter.adminId) }
            ) {
                Text(
                    text     = "Admin: ${shelter.adminName ?: shelter.adminId}",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Estadísticas (en adopción real, publicaciones y seguidores estéticos)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatColumn(label = "En adopción",   value = shelter.animals.size.toString(), onClick = {})
            ProfileStatColumn(label = "Publicaciones", value = "0", onClick = {})
            ProfileStatColumn(label = "Seguidores",    value = "0", onClick = {})
        }

        Spacer(Modifier.height(16.dp))

        // ── Descripción
        val descriptionBg = if (isSystemInDarkTheme()) PawpSurfaceDark else Color(0xFFF0F0F0)
        Surface(
            shape    = MaterialTheme.shapes.medium,
            color    = descriptionBg,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (shelter.description.isNotBlank()) {
                Text(
                    text     = shelter.description,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text      = "Esta protectora aún no tiene descripción.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontStyle = FontStyle.Italic,
                    modifier  = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Datos de contacto
        ShelterInfoRow("Teléfono", shelter.phone)
        ShelterInfoRow("Email", shelter.email)
        shelter.address?.let { ShelterInfoRow("Dirección", it) }
        shelter.website?.let { ShelterInfoRow("Web", it) }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // ── Sección de publicaciones (placeholder)
        Text("Publicaciones", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Surface(
            shape    = MaterialTheme.shapes.medium,
            color    = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Box(Modifier.padding(12.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    text  = "Próximamente",
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
