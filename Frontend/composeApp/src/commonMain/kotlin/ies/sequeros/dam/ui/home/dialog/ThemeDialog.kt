package ies.sequeros.dam.ui.home.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.appsettings.ThemeMode

@Composable
fun ThemeDialog(

    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {

    val options = listOf(

        "Usar el tema del sistema" to ThemeMode.SYSTEM,
        "Modo claro" to ThemeMode.LIGHT,
        "Modo oscuro" to ThemeMode.DARK
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text  = "Apariencia",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                options.forEach { (label, mode) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{ onModeSelected(mode)}
                            .padding(vertical = 4.dp)
                    ){
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onModeSelected(mode)}
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(

                            text = label,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }


            }
        },
        confirmButton = {
            TextButton( onClick = onDismiss){

                Text("Cerrar")
            }
        }
    )
}