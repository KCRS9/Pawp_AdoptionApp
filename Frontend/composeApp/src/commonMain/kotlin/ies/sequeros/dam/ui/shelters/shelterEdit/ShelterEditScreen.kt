package ies.sequeros.dam.ui.shelters.shelterEdit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShelterEditScreen(onBack: () -> Unit) {

    val viewModel: ShelterEditViewModel = koinViewModel()
    val appViewModel: AppViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // Pre-cargamos los datos de la protectora al entrar por primera vez
    LaunchedEffect(currentUser?.shelterId) {

        currentUser?.shelterId?.let { viewModel.init(it) }
    }

    LaunchedEffect(state.isSuccess) {

        if (state.isSuccess) {

            snackbarHost.showSnackbar("Protectora actualizada correctamente.")
            onBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        
        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    SettingsFormScaffold(
        title = "Editar protectora",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        // Nombre — obligatorio
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

        // Teléfono — obligatorio
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

        // Email — obligatorio
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

        // Dirección — opcional
        OutlinedTextField(
            value = state.address,
            onValueChange = viewModel::onAddressChange,
            label = { Text("Dirección (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Web — opcional
        OutlinedTextField(
            value = state.website,
            onValueChange = viewModel::onWebsiteChange,
            label = { Text("Página web (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Descripción — opcional
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
