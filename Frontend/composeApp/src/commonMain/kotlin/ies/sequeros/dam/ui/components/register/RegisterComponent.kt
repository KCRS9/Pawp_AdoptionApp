package ies.sequeros.dam.ui.components.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.register.RegisterState
import ies.sequeros.dam.ui.theme.PawpPurple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.focus.onFocusChanged
import ies.sequeros.dam.ui.components.common.LocalityDropdown


@Composable
fun RegisterComponent(
    state: RegisterState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onLocationSelect: (id: Int, name: String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    onIsShelterChange: (Boolean) -> Unit,
    onShelterNameChange: (String) -> Unit,
    onShelterDescriptionChange: (String) -> Unit,
    onShelterPhoneChange: (String) -> Unit,
    onShelterEmailChange: (String) -> Unit,
) {

    var emailFocused           by remember { mutableStateOf(false) }
    var emailTouched           by remember { mutableStateOf(false) }
    var passwordFocused        by remember { mutableStateOf(false) }
    var passwordTouched        by remember { mutableStateOf(false) }
    var confirmPasswordFocused by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    var shelterNameFocused  by remember { mutableStateOf(false) }
    var shelterNameTouched  by remember { mutableStateOf(false) }
    var shelterEmailFocused by remember { mutableStateOf(false) }
    var shelterEmailTouched by remember { mutableStateOf(false) }
    var shelterPhoneFocused by remember { mutableStateOf(false) }
    var shelterPhoneTouched by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            PawpCard(showImage = true)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Registrate",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Nombre
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it) } },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { fs ->
                        if (fs.isFocused) emailFocused = true
                        else if (emailFocused) emailTouched = true
                    },
                isError = emailTouched && state.emailError != null,
                supportingText = { if (emailTouched) state.emailError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { fs ->
                        if (fs.isFocused) passwordFocused = true
                        else if (passwordFocused) passwordTouched = true
                    },
                isError = passwordTouched && state.passwordError != null,
                supportingText = { if (passwordTouched) state.passwordError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { fs ->
                        if (fs.isFocused) confirmPasswordFocused = true
                        else if (confirmPasswordFocused) confirmPasswordTouched = true
                    },
                isError = confirmPasswordTouched && state.confirmPasswordError != null,
                supportingText = { if (confirmPasswordTouched) state.confirmPasswordError?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(8.dp))

            LocalityDropdown(
                localities = state.localities,
                selectedName = state.locationName,
                onSelect = onLocationSelect,
                isError = state.locationError != null,
                errorMessage = state.locationError
            )

            Spacer(Modifier.height(24.dp))

            if(state.errorMessage != null){
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

// Casilla para activar el registro como protectora
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.isShelter,
                    onCheckedChange = onIsShelterChange,
                    colors = CheckboxDefaults.colors(checkedColor = PawpPurple)
                )
                Text(
                    text = "Soy una protectora",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

// los campos de protectora se despliegan con animacion al marcar la casilla
            AnimatedVisibility(visible = state.isShelter) {
                Column {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Datos de la protectora",
                        style = MaterialTheme.typography.titleSmall,
                        color = PawpPurple
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.shelterName,
                        onValueChange = onShelterNameChange,
                        label = { Text("Nombre de la protectora") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { fs ->
                                if (fs.isFocused) shelterNameFocused = true
                                else if (shelterNameFocused) shelterNameTouched = true
                            },
                        isError = shelterNameTouched && state.shelterNameError != null,
                        supportingText = { if (shelterNameTouched) state.shelterNameError?.let { Text(it) } },
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.shelterPhone,
                        onValueChange = onShelterPhoneChange,
                        label = { Text("Teléfono de contacto") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { fs ->
                                if (fs.isFocused) shelterPhoneFocused = true
                                else if (shelterPhoneFocused) shelterPhoneTouched = true
                            },
                        isError = shelterPhoneTouched && state.shelterPhoneError != null,
                        supportingText = { if (shelterPhoneTouched) state.shelterPhoneError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.shelterEmail,
                        onValueChange = onShelterEmailChange,
                        label = { Text("Correo de la protectora") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { fs ->
                                if (fs.isFocused) shelterEmailFocused = true
                                else if (shelterEmailFocused) shelterEmailTouched = true
                            },
                        isError = shelterEmailTouched && state.shelterEmailError != null,
                        supportingText = { if (shelterEmailTouched) state.shelterEmailError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.shelterDescription,
                        onValueChange = onShelterDescriptionChange,
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !state.isLoading,
                    border   = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = PawpPurple
                    )
                ){
                    Text(text = "Volver",
                        color = PawpPurple)
                }

                Button(
                    onClick  = onRegisterClick,
                    modifier = Modifier.weight(1f),
                    enabled  = state.isValid && !state.isLoading,
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = PawpPurple,
                        contentColor   = Color.White
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarse")
                    }
                }
            }
        }
    }
}