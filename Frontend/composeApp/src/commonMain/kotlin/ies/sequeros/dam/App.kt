package ies.sequeros.dam

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.home.HomeScreen
import ies.sequeros.dam.ui.login.LoginScreen
import ies.sequeros.dam.ui.register.RegisterScreen
import ies.sequeros.dam.ui.theme.PawpTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {

    val appViewModel: AppViewModel = koinViewModel()
    val isLoggedIn by appViewModel.isLoggedIn.collectAsStateWithLifecycle()

    var authScreen by remember { mutableStateOf("login") }

    PawpTheme {
    // MaterialTheme {
        Surface {
            when (isLoggedIn){

                null -> {/*Un SplashScreen*/}
                
                true -> HomeScreen()

                else -> {
                    when(authScreen){
                        "login" -> LoginScreen(
                            onLoginSuccess = {appViewModel.notifyLogin()},
                            onGoToRegister = {authScreen = "register"}
                        )

                        "register" -> RegisterScreen(
                            onRegisterSuccess = {authScreen = "login"},
                            onGoToLogin = {authScreen="login"}
                        )
                    }
                }
            }
        }
    }
}