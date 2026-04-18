package ies.sequeros.dam.ui.settings.changePassword

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
fun ChangePasswordScreen(onBack: () -> Unit) {

    val viewModel: ChangePasswordViewModel = koinViewModel()

    val appViewModel: AppViewModel = koinViewModel()
    var newPasswordFocused     by remember { mutableStateOf(false) }
    var newPasswordTouched     by remember { mutableStateOf(false) }
    var confirmPasswordFocused by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHost.showSnackbar("Contraseña cambiada. Inicia sesión de nuevo.")
            appViewModel.logout()
        }
    }

    LaunchedEffect(state.errorMessage) {

        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    SettingsFormScaffold(

        title = "Cambiar contraseña",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        PawpCard(showImage = true)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.oldPassword,
            onValueChange = viewModel::onOldPasswordChange,
            label = { Text("Contraseña actual") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.newPassword,
            onValueChange = viewModel::onNewPasswordChange,
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { fs ->
                    if (fs.isFocused) newPasswordFocused = true
                    else if (newPasswordFocused) newPasswordTouched = true
                },
            isError = newPasswordTouched && state.newPasswordError != null,
            supportingText = { if (newPasswordTouched) state.newPasswordError?.let { Text(it) } },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirmar nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { fs ->
                    if (fs.isFocused) confirmPasswordFocused = true
                    else if (confirmPasswordFocused) confirmPasswordTouched = true
                },
            isError = confirmPasswordTouched && state.confirmPasswordError != null,
            supportingText = { if (confirmPasswordTouched) state.confirmPasswordError?.let { Text(it) } },
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::changePassword,
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

                Text("Guardar contraseña")
            }
        }
    }
}