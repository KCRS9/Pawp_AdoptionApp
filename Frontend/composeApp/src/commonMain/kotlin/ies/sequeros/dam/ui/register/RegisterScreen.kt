package ies.sequeros.dam.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.register.RegisterComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegisterSuccess) {
        if (state.isRegisterSuccess) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    RegisterComponent(
        state                   = state,
        onNameChange            = viewModel::onNameChange,
        onEmailChange           = viewModel::onEmailChange,
        onPasswordChange        = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onLocationChange        = viewModel::onLocationChange,
        onRegisterClick         = viewModel::register,
        onBackClick             = onGoToLogin
    )
}
