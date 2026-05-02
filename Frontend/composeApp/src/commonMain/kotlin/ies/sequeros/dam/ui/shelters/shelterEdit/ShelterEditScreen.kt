package ies.sequeros.dam.ui.shelters.shelterEdit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.common.showBrief
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShelterEditScreen(shelterId: String, onBack: () -> Unit) {

    val viewModel: ShelterEditViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // precargamos datos al entrar por primera vez
    LaunchedEffect(shelterId) {
        if (shelterId.isNotBlank()) viewModel.init(shelterId)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHost.showBrief("Protectora actualizada correctamente.")
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.isPhotoSuccess }.collect { success ->
            if (success) {
                viewModel.onPhotoSuccessHandled()
                snackbarHost.showBrief("Logo actualizado correctamente")
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    // abre el selector → guarda bytes para previsualizar (no sube todavia)
    val logoLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        viewModel.onLogoFileSelected(file)
    }

    SettingsFormScaffold(
        title = "Editar protectora",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        AvatarWithPencil(
            imageUrl     = state.profileImage,
            previewBytes = state.previewBytes,
            size         = 96.dp,
            isUploading  = state.isUploadingPhoto,
            onEditClick  = { logoLauncher.launch() },
            modifier     = Modifier.align(Alignment.CenterHorizontally)
        )

        // botones confirmar / cancelar visibles solo cuando hay previsualizacion pendiente
        if (state.previewBytes != null) {
            Row(
                modifier              = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.onLogoFileSelected(null) }) {
                    Text("Cancelar")
                }
                Button(onClick = viewModel::confirmLogo) {
                    Text("Confirmar")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

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

        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = state.phoneError != null,
            supportingText = { state.phoneError?.let { Text(it) } }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email de contacto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(it) } }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.address,
            onValueChange = viewModel::onAddressChange,
            label = { Text("Dirección (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.website,
            onValueChange = viewModel::onWebsiteChange,
            label = { Text("Página web (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Descripción") },
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
            if (state.isLoading)
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else
                Text("Guardar cambios")
        }
    }
}
