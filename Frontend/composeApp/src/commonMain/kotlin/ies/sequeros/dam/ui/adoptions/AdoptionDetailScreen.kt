package ies.sequeros.dam.ui.adoptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionDetailScreen(
    adoptionId: Int,
    isShelter: Boolean,
    onBack: () -> Unit,
    onAnimalClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val viewModel: AdoptionDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(adoptionId) { viewModel.load(adoptionId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                state.isLoading || state.adoption == null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val a = state.adoption!!
                    val contactParts = a.contact?.split("|") ?: emptyList()
                    val phone = contactParts.getOrNull(0)?.trim()
                    val sharedEmail = contactParts.getOrNull(1)?.trim()

                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))

                        // Mini-ficha del animal (clicable)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAnimalClick(a.animalId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (!a.animalImage.isNullOrBlank()) {
                                    AsyncImage(
                                        model = a.animalImage,
                                        contentDescription = a.animalName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        a.animalName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        a.shelterName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AdoptionStatusChip(a.status)
                            }
                        }

                        if (isShelter) {
                            HorizontalDivider()

                            // Mini-ficha del solicitante (solo visible para la protectora)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUserClick(a.userId) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (!a.userImage.isNullOrBlank()) {
                                        AsyncImage(
                                            model = a.userImage,
                                            contentDescription = a.userName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(28.dp))
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            a.userName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (!a.userLocation.isNullOrBlank()) {
                                            Text(
                                                a.userLocation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        // Datos de contacto
                        if (!phone.isNullOrBlank()) DetailField("Teléfono", phone)
                        if (!sharedEmail.isNullOrBlank()) DetailField("Correo electrónico", sharedEmail)
                        a.housingType?.let { DetailField("Tipo de vivienda", it) }
                        a.otherAnimals?.let { DetailField("Otros animales", if (it) "Sí" else "No") }
                        a.hoursAlone?.let { DetailField("Horas solo al día", "$it h") }
                        a.experience?.let { DetailField("Experiencia previa", it) }
                        DetailField("Motivación", a.motivation)

                        // Botones de estado — solo para la protectora
                        if (isShelter) {
                            HorizontalDivider()
                            Text("Cambiar estado", style = MaterialTheme.typography.labelLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (a.status) {
                                    "pending" -> Button(
                                        onClick = { viewModel.updateStatus(a.id, "reviewing") },
                                        enabled = !state.isUpdating
                                    ) { Text("En revisión") }
                                    "reviewing" -> {
                                        Button(
                                            onClick = { viewModel.updateStatus(a.id, "approved") },
                                            enabled = !state.isUpdating,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF66BB6A)
                                            )
                                        ) { Text("Aprobar") }
                                        OutlinedButton(
                                            onClick = { viewModel.updateStatus(a.id, "rejected") },
                                            enabled = !state.isUpdating
                                        ) { Text("Rechazar") }
                                    }
                                    "approved" -> Button(
                                        onClick = { viewModel.updateStatus(a.id, "completed") },
                                        enabled = !state.isUpdating
                                    ) { Text("Marcar completada") }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
