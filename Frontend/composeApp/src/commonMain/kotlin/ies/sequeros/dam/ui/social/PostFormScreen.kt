package ies.sequeros.dam.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ies.sequeros.dam.domain.models.AnimalSummary
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.common.showBrief
import ies.sequeros.dam.ui.theme.PawpPurple
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostFormScreen(
    onBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    val viewModel: PostFormViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.reset() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHost.showBrief("¡Publicación creada!")
            viewModel.onSuccessHandled()
            onPostCreated()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    val photoLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        viewModel.onPhotoSelected(file)
    }

    var showAnimalDialog by remember { mutableStateOf(false) }

    SettingsFormScaffold(
        title = "Nueva publicación",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        PawpCard(modifier = Modifier.padding(bottom = 16.dp))

        // Selector de foto — grande y rectangular (sin bordes redondeados)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RectangleShape
                )
                .clickable { photoLauncher.launch() },
            contentAlignment = Alignment.Center
        ) {
            if (state.photoBytes != null) {
                AsyncImage(
                    model = state.photoBytes,
                    contentDescription = "Foto seleccionada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { photoLauncher.launch() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Cambiar foto",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Toca para añadir una foto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.text,
            onValueChange = viewModel::onTextChange,
            label = { Text("¿Qué quieres contar? (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )

        Spacer(Modifier.height(12.dp))

        if (state.selectedAnimalId == null) {
            OutlinedButton(
                onClick = {
                    viewModel.loadAnimals()
                    showAnimalDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🐾 Etiquetar un animal (opcional)")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "🐾 ${state.selectedAnimalName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = PawpPurple,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                IconButton(onClick = viewModel::clearAnimal) {
                    Icon(Icons.Default.Close, contentDescription = "Quitar animal")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::post,
            enabled = state.canPost,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Publicar")
        }
    }

    if (showAnimalDialog) {
        AnimalSelectorDialog(
            animals = state.animals,
            filter = state.animalsFilter,
            isLoading = state.isLoadingAnimals,
            selectedAnimalId = state.selectedAnimalId,
            onFilterChange = viewModel::onAnimalsFilterChange,
            onSelect = { id, name ->
                viewModel.selectAnimal(id, name)
                showAnimalDialog = false
            },
            onClear = {
                viewModel.clearAnimal()
                showAnimalDialog = false
            },
            onDismiss = { showAnimalDialog = false }
        )
    }
}

@Composable
private fun AnimalSelectorDialog(
    animals: List<AnimalSummary>,
    filter: String,
    isLoading: Boolean,
    selectedAnimalId: String?,
    onFilterChange: (String) -> Unit,
    onSelect: (String, String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val filtered = remember(animals, filter) {
        if (filter.isBlank()) animals
        else animals.filter { it.name.contains(filter, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Etiquetar animal", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = filter,
                    onValueChange = onFilterChange,
                    label = { Text("Buscar por nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filtered, key = { it.id }) { animal ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(animal.id, animal.name) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = animal.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (animal.id == selectedAnimalId) PawpPurple
                                            else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = animal.species,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedAnimalId != null) {
                        TextButton(onClick = onClear) { Text("Quitar etiqueta") }
                    }
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            }
        }
    }
}
