package ies.sequeros.dam.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.common.showBrief
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel

private val ROLES = listOf("user", "shelter", "admin")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserEditScreen(userId: String, onBack: () -> Unit) {

    val viewModel: AdminUserEditViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(userId) { if (userId.isNotBlank()) viewModel.init(userId) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHost.showBrief("Usuario actualizado correctamente")
            viewModel.onSuccessHandled()
            onBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    val photoLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        viewModel.onPhotoSelected(file)
    }

    var roleExpanded by remember { mutableStateOf(false) }

    SettingsFormScaffold(
        title = "Editar usuario",
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

        if (state.previewBytes != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.onPhotoSelected(null) }) { Text("Cancelar") }
                Button(onClick = viewModel::confirmPhoto) { Text("Confirmar foto") }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = roleExpanded,
            onExpandedChange = { roleExpanded = it }
        ) {
            OutlinedTextField(
                value = state.role,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = { roleExpanded = false }
            ) {
                ROLES.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol) },
                        onClick = { viewModel.onRoleChange(rol); roleExpanded = false }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::save,
            enabled = state.name.isNotBlank() && state.email.isNotBlank() && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Guardar cambios")
        }
    }
}
