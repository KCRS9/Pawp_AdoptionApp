package ies.sequeros.dam.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.LocalityDropdown
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.components.common.showBrief
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {

    val viewModel: EditProfileViewModel = koinViewModel()
    val appViewModel: AppViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }

    // cuando el perfil se guarda → refrescamos sesion y volvemos
    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            snackbarHost.showBrief("Perfil actualizado correctamente.")
            appViewModel.refreshCurrentUser()
            viewModel.onSaveSuccessHandled()
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.isPhotoSuccess }
            .collect { success ->
                if (success) {
                    viewModel.onPhotoSuccessHandled()
                    appViewModel.refreshCurrentUser()
                    snackbarHost.showBrief("Foto actualizada correctamente")
                }
            }
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    // selector de imagen → guarda bytes para previsualizar (no sube todavia)
    val imageLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        viewModel.onAvatarFileSelected(file)
    }

    SettingsFormScaffold(
        title        = "Editar perfil",
        onBack       = onBack,
        snackbarHost = snackbarHost
    ) {

        AvatarWithPencil(
            imageUrl     = currentUser?.profileImage,
            previewBytes = state.previewBytes,
            size         = 96.dp,
            isUploading  = state.isUploadingPhoto,
            onEditClick  = { imageLauncher.launch() },
            modifier     = Modifier.align(Alignment.CenterHorizontally)
        )

        // Confirmar / Cancelar visibles solo cuando hay imagen seleccionada pendiente
        if (state.previewBytes != null) {
            Row(
                modifier              = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.onAvatarFileSelected(null) }) {
                    Text("Cancelar")
                }
                Button(onClick = viewModel::confirmAvatar) {
                    Text("Confirmar")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Nombre
        OutlinedTextField(
            value          = state.name,
            onValueChange  = viewModel::onNameChange,
            label          = { Text("Nombre") },
            modifier       = Modifier.fillMaxWidth(),
            isError        = state.nameError != null,
            supportingText = { state.nameError?.let { Text(it) } },
            singleLine     = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value         = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label         = { Text("Sobre mí") },
            modifier      = Modifier.fillMaxWidth().height(120.dp),
            maxLines      = 5
        )

        Spacer(Modifier.height(8.dp))

        // Provincia
        LocalityDropdown(
            localities   = state.localities,
            selectedName = state.locationName,
            onSelect     = viewModel::onLocationSelect
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick  = viewModel::saveProfile,
            enabled  = state.isValid && !state.isSaving,
            shape    = androidx.compose.material3.MaterialTheme.shapes.medium,
            colors   = ButtonDefaults.buttonColors(
                containerColor = PawpPurpleDark,
                contentColor   = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Guardar cambios")
            }
        }
    }
}
