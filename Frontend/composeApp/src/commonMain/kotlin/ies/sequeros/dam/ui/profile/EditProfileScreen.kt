package ies.sequeros.dam.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.LocalityDropdown
import ies.sequeros.dam.ui.components.common.UserAvatar
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {

    val viewModel: EditProfileViewModel = koinViewModel()
    val appViewModel: AppViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }

    // Cuando el perfil se guarda → refrescamos sesión y volvemos
    LaunchedEffect(state.isSaveSuccess) {

        if (state.isSaveSuccess) {
            appViewModel.refreshCurrentUser()
            viewModel.onSaveSuccessHandled()
            onBack()
        }
    }

    // Cuando la foto se sube refrescamos sesión (la pantalla no cierra)
    LaunchedEffect(state.isPhotoSuccess) {

        if (state.isPhotoSuccess) {
            appViewModel.refreshCurrentUser()
            viewModel.onPhotoSuccessHandled()
        }
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(state.errorMessage) {

        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    // Launcher de FileKit — se abre al pulsar el icono de lápiz del avatar
    val imageLauncher = rememberFilePickerLauncher(
        type = PickerType.Image

    ) {
        file ->
        file?.let { viewModel.onAvatarSelected(it) }
    }

    Scaffold(
        topBar = {

            TopAppBar(

                title = { Text("Editar perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(snackbarHost) }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Avatar con icono de lápiz superpuesto
            Box {
                // Foto actual (o icono por defecto si no tiene)
                if (state.isUploadingPhoto) {
                    CircularProgressIndicator(
                        color    = PawpPurple.copy(alpha = 0.5f),
                        modifier = Modifier.size(96.dp)
                    )
                } else {
                    UserAvatar(imageUrl = currentUser?.profileImage, size = 96.dp)
                }

                // Botón de lápiz en la esquina inferior derecha
                IconButton(
                    onClick  = { imageLauncher.launch() },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .background(PawpPurpleDark, CircleShape)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Edit,
                        contentDescription = "Cambiar foto",
                        tint               = Color.White,
                        modifier           = Modifier.size(16.dp)
                    )
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

            // Descripción
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

            // Botón guardar
            Button(
                onClick  = viewModel::saveProfile,
                enabled  = state.isValid && !state.isSaving,
                shape    = MaterialTheme.shapes.medium,
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
}