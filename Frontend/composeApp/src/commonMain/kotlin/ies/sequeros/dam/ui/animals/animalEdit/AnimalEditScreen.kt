package ies.sequeros.dam.ui.animals.animalEdit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.common.showBrief
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

private val SPECIES_OPTIONS = listOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro")
private val GENDER_OPTIONS = listOf("male" to "Macho", "female" to "Hembra", "unknown" to "Desconocido")
private val SIZE_OPTIONS = listOf("small" to "Pequeño", "medium" to "Mediano", "large" to "Grande")
private val STATUS_OPTIONS = listOf(
    "available" to "Disponible",
    "adopted" to "Adoptado",
    "reserved" to "Reservado",
    "other" to "Otro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEditScreen(
    animalId: String? = null,
    onBack: () -> Unit,
    onSaved: (String) -> Unit = { onBack() },
    onDeleted: () -> Unit = onBack
) {
    val viewModel: AnimalEditViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(animalId) {
        if (animalId != null) viewModel.initEdit(animalId)
        else viewModel.initCreate()
    }

    LaunchedEffect(state.createdAnimalId) {
        state.createdAnimalId?.let { id ->
            snackbarHost.showBrief("¡Animal creado correctamente!")
            onSaved(id)
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            snackbarHost.showBrief("¡Cambios guardados correctamente!")
            onBack()
        }
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onDeleted()
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.isPhotoSuccess }.collect { success ->
            if (success) {
                viewModel.onPhotoSuccessHandled()
                snackbarHost.showBrief("Foto actualizada correctamente")
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    val photoLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        viewModel.onPhotoFileSelected(file)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar animal") },
            text = { Text("¿Seguro que quieres eliminar a ${state.name}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.delete() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.fromEpochDays((millis / 86_400_000L).toInt()).toString()
                        viewModel.onBirthDateChange(date)
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    SettingsFormScaffold(
        title = if (state.isCreateMode) "Añadir animal" else "Editar animal",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        AvatarWithPencil(
            imageUrl = state.profileImage,
            previewBytes = state.previewBytes,
            size = 96.dp,
            isUploading = state.isUploadingPhoto,
            onEditClick = { photoLauncher.launch() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (!state.isCreateMode && state.previewBytes != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.onPhotoFileSelected(null) }) { Text("Cancelar") }
                Button(onClick = viewModel::confirmPhoto) { Text("Confirmar foto") }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.nameError != null,
            supportingText = { state.nameError?.let { Text(it) } }
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdown(
            label = "Especie",
            options = SPECIES_OPTIONS,
            selected = state.species,
            onSelect = viewModel::onSpeciesChange
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.breed,
            onValueChange = viewModel::onBreedChange,
            label = { Text("Raza") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.breedError != null,
            supportingText = { state.breedError?.let { Text(it) } }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.birthDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento (opcional)") },
            placeholder = { Text("Sin fecha", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdown(
            label = "Género",
            options = GENDER_OPTIONS.map { it.second },
            selected = GENDER_OPTIONS.firstOrNull { it.first == state.gender }?.second ?: "Desconocido",
            onSelect = { label ->
                GENDER_OPTIONS.firstOrNull { it.second == label }?.first?.let(viewModel::onGenderChange)
            }
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdown(
            label = "Tamaño",
            options = SIZE_OPTIONS.map { it.second },
            selected = SIZE_OPTIONS.firstOrNull { it.first == state.size }?.second ?: "Pequeño",
            onSelect = { label ->
                SIZE_OPTIONS.firstOrNull { it.second == label }?.first?.let(viewModel::onSizeChange)
            }
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdown(
            label = "Estado",
            options = STATUS_OPTIONS.map { it.second },
            selected = STATUS_OPTIONS.firstOrNull { it.first == state.status }?.second ?: "Disponible",
            onSelect = { label ->
                STATUS_OPTIONS.firstOrNull { it.second == label }?.first?.let(viewModel::onStatusChange)
            }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.health,
            onValueChange = viewModel::onHealthChange,
            label = { Text("Salud (vacunas, esterilizado...)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::save,
            enabled = state.isValid && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text(if (state.isCreateMode) "Crear animal" else "Guardar cambios")
        }

        if (!state.isCreateMode) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar animal")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}
