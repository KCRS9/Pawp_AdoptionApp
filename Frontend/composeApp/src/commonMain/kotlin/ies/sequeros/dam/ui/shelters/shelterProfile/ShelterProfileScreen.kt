package ies.sequeros.dam.ui.shelters.shelterProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfileScreen(

    shelterId: String,
    onBack: () -> Unit,
    // null = vista de solo lectura (listado público)
    // non-null = el usuario es admin de esta protectora, se muestra el lápiz
    onEditClick: (() -> Unit)? = null,
    onAdminClick: (String) -> Unit = {}
) {
    val viewModel: ShelterProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Cargamos la protectora al entrar a la pantalla usando el id como clave
    LaunchedEffect(shelterId) { viewModel.load(shelterId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.shelter?.name ?: "Protectora") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val shelter = state.shelter ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo con lápiz superpuesto si el usuario es el admin de la protectora
            Box {
                if (shelter.profileImage != null) {
                    AsyncImage(
                        model = shelter.profileImage,
                        contentDescription = shelter.name,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.medium),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                shelter.name.take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }

                // El lápiz solo aparece cuando este usuario administra esta protectora
                if (onEditClick != null) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar protectora")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(shelter.name, style = MaterialTheme.typography.headlineSmall)
            
            Spacer(Modifier.height(24.dp))

            ShelterInfoRow("Teléfono", shelter.phone)
            
            ShelterInfoRow("Email", shelter.email)
            
            shelter.address?.let { ShelterInfoRow("Dirección", it) }
            
            shelter.website?.let { ShelterInfoRow("Web", it) }

            Spacer(Modifier.height(12.dp))
            
            Text(shelter.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))

            // Card del administrador — al pulsar va a su perfil de usuario
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAdminClick(shelter.adminId) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Administrador",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            shelter.adminName ?: shelter.adminId,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}
