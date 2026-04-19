package ies.sequeros.dam.ui.animals.animalDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.domain.models.toAgeString
import ies.sequeros.dam.ui.components.common.AvatarWithPencil
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.pawpOnSurfaceTextColor
import ies.sequeros.dam.ui.theme.pawpSurfaceColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnimalDetailScreen(
    animalId: String,
    onBack: () -> Unit,
    onShelterClick: (String) -> Unit = {},
    onEditClick: (() -> Unit)? = null
) {
    val viewModel: AnimalDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(animalId) { viewModel.load(animalId) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, animalId) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.reload(animalId)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    SettingsFormScaffold(
        title = state.animal?.name ?: "Animal",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@SettingsFormScaffold
        }

        val animal = state.animal ?: return@SettingsFormScaffold

        AvatarWithPencil(
            imageUrl = animal.profileImage,
            size = 196.dp,
            onEditClick = onEditClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = animal.name,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            animal.locationName?.let { loc ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.52f)
                ) {
                    Text(
                        text = loc,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.clickable { onShelterClick(animal.shelterId) }
            ) {
                Text(
                    text = animal.shelterName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val surfaceBg = pawpSurfaceColor()
        val surfaceText = pawpOnSurfaceTextColor()

        val genderLabel = when (animal.gender) {
            "male" -> "Macho"
            "female" -> "Hembra"
            else -> "Desconocido"
        }
        val sizeLabel = when (animal.size) {
            "small" -> "Pequeño"
            "medium" -> "Mediano"
            "large" -> "Grande"
            else -> animal.size
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surfaceBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    AnimalDataCell("Especie", animal.species, surfaceText)
                    AnimalDataCell("Género", genderLabel, surfaceText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    AnimalDataCell("Tamaño", sizeLabel, surfaceText)
                    AnimalDataCell("Raza", animal.breed.ifBlank { "—" }, surfaceText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    AnimalDataCell("Edad", animal.birthDate.toAgeString(), surfaceText)
                }
            }
        }

        if (animal.health.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = animal.health,
                        style = MaterialTheme.typography.bodyMedium,
                        color = surfaceText
                    )
                    Text(
                        text = "Salud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (animal.description.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(text = "Sobre mí", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = animal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = surfaceText,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimalDataCell(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = textColor)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
