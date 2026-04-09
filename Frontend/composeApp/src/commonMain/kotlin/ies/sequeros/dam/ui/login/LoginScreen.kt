package ies.sequeros.dam.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.LoginComponent
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun LoginScreen (
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
){
    val viewModel: LoginViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoginSuccess){
        if(state.isLoginSuccess){
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    LoginComponent(
        state = state,
        onEmailChenge = viewModel::onEmailChange,
        onPasswordChange = viewModel::onEmailChange,
        onLoginClick = viewModel::login,
        onRegisterClick = onGoToRegister
    )

}