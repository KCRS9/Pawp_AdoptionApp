package ies.sequeros.dam.ui.components.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.login.LoginState
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import ies.sequeros.dam.ui.theme.PawpPurpleLight
import org.jetbrains.compose.resources.painterResource
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.logo_pawp

@Composable
fun LoginComponent(
    state: LoginState,
    onEmailChenge: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PawpPurpleDark),
        contentAlignment = Alignment.Center
    ){

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            PawpCard(showImage = false)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChenge,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor     = Color.White.copy(alpha = 0.8f),
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor     = Color.White.copy(alpha = 0.8f),
                )
            )


            Spacer(Modifier.height(8.dp))

            if(state.errorMessage != null) {

                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
            }

            ElevatedButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isValid && !state.isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PawpPurpleDark,
                    contentColor = Color.White
                ),

            ){
                if(state.isLoading){

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                }else{
                    Text("Entrar")
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onRegisterClick,
                enabled = !state.isValid,
            ){
                Text(
                    text ="¿No tienes cuenta? Regístrate",
                    color = Color.White)

            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {




                Text(
                    text  = "Y unete a la comunidad mas grande de adopción de animales",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(Res.drawable.logo_pawp),
                    contentDescription = "Logo Pawp",
                    modifier = Modifier
                        .size(80.dp)
//                        .align(Alignment.BottomEnd)
//                        .padding(24.dp)
                )





            }



        }



    }
}



