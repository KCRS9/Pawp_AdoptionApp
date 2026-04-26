package ies.sequeros.dam.ui.adoptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.showBrief
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionFormScreen(
    animalId: String,
    animalName: String,
    userEmail: String,
    onBack: () -> Unit
) {
    val viewModel: AdoptionFormViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(animalId) { viewModel.reset() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHost.showBrief("¡Solicitud enviada correctamente!")
            viewModel.onSuccessHandled()
            onBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showBrief(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar adopción") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Solicitud para adoptar a $animalName",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = { Text("Teléfono de contacto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Deseo compartir mi correo electrónico",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = state.shareEmail,
                        onCheckedChange = viewModel::onShareEmailChange
                    )
                }

                OutlinedTextField(
                    value = state.housingType,
                    onValueChange = viewModel::onHousingTypeChange,
                    label = { Text("Tipo de vivienda (piso, casa con jardín...)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Tienes otros animales?", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = state.otherAnimals,
                        onCheckedChange = viewModel::onOtherAnimalsChange
                    )
                }

                OutlinedTextField(
                    value = state.hoursAlone,
                    onValueChange = viewModel::onHoursAloneChange,
                    label = { Text("Horas solo al día") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.experience,
                    onValueChange = viewModel::onExperienceChange,
                    label = { Text("Experiencia previa con animales") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = state.motivation,
                    onValueChange = viewModel::onMotivationChange,
                    label = { Text("¿Por qué quieres adoptarlo?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.submit(animalId, userEmail) },
                    enabled = state.isFormValid && !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp))
                    else Text("Enviar solicitud")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
