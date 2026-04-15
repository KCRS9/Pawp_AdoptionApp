package ies.sequeros.dam.ui.components.common

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import ies.sequeros.dam.domain.models.Locality

/**
 * Dropdown de provincia reutilizable.
 *
 * @param localities     Lista de localidades obtenida del servidor.
 * @param selectedName   Nombre de la localidad actualmente seleccionada (texto del campo).
 * @param onSelect       Callback con (id, name) cuando el usuario elige una opción.
 * @param isError        Si true, el campo se pinta en rojo.
 * @param errorMessage   Mensaje de error bajo el campo (null = no mostrar).
 * @param modifier       Modificador externo para ajustar tamaño o posición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalityDropdown(
    localities: List<Locality>,
    selectedName: String,
    onSelect: (id: Int, name: String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded          = expanded,
        onExpandedChange  = { expanded = it },
        modifier          = modifier
    ) {
        OutlinedTextField(
            value          = selectedName,
            onValueChange  = {},              // readOnly — el usuario solo puede seleccionar
            readOnly       = true,
            label          = { Text("Provincia") },
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier       = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            isError        = isError,
            supportingText = { errorMessage?.let { Text(it) } }
        )

        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            localities.forEach { locality ->
                DropdownMenuItem(
                    text    = { Text(locality.name) },
                    onClick = {
                        onSelect(locality.id, locality.name)
                        expanded = false
                    }
                )
            }
        }
    }
}