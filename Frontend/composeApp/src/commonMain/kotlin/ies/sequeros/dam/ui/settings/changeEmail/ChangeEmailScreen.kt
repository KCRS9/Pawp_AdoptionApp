package ies.sequeros.dam.ui.settings.changeEmail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(onBack: () -> Unit) {

    val viewModel: ChangeEmailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {

        if (state.isSuccess) {
            snackbarHost.showSnackbar("Contraseña cambiada correctamente.")
            onBack()}
    }

    LaunchedEffect(state.errorMessage) {

        //snackbarHost.showSnackbar("Correo actualizado correctamente")
        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Cambiar correo electrónico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(snackbarHost) }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ){

            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {

                PawpCard(showImage = true)

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.newEmail,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Nuevo correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.emailError != null,
                    supportingText = { state.emailError?.let { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(8.dp))

                // Pedimos la contraseña actual para confirmar la identidad del usuario
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Contraseña actual") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = viewModel::changeEmail,
                    enabled = state.isValid && !state.isLoading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PawpPurpleDark,
                        contentColor   = Color.White
                    ),

                    modifier = Modifier.fillMaxWidth()

                ) {
                    if (state.isLoading) {

                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {

                        Text("Guardar correo")
                    }
                }
            }
        }
    }
}