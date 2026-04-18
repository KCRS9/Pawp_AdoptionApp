package ies.sequeros.dam.ui.settings.changeEmail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangeEmailScreen(onBack: () -> Unit) {

    val viewModel: ChangeEmailViewModel = koinViewModel()
    val appViewModel: AppViewModel = koinViewModel()
    var emailTouched by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {

        if (state.isSuccess) {

            snackbarHost.showSnackbar("Correo actualizado. Inicia sesión de nuevo.")
            appViewModel.logout()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    SettingsFormScaffold(

        title = "Cambiar correo electrónico",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        PawpCard(showImage = true)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(

            value = state.newEmail,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Nuevo correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (!it.isFocused) emailTouched = true },
            isError = emailTouched && state.emailError != null,
            supportingText = { if (emailTouched) state.emailError?.let { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(8.dp))

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
                containerColor = PawpPurple,
                contentColor   = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {

                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )

            } else {

                Text("Guardar correo")
            }
        }
    }
}