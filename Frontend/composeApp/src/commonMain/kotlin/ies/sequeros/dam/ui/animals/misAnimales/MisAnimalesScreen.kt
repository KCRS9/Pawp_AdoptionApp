package ies.sequeros.dam.ui.animals.misAnimales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AnimalMiniCard
import ies.sequeros.dam.ui.components.common.SettingsFormScaffold
import org.koin.compose.viewmodel.koinViewModel

private val STATUS_FILTERS = listOf(
    "available" to "Disponibles",
    "adopted" to "Adoptados",
    "reserved" to "Reservados",
    "other" to "Otros"
)

@Composable
fun MisAnimalesScreen(
    shelterId: String,
    onAnimalClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: MisAnimalesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(shelterId) { viewModel.load(shelterId) }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHost.showSnackbar(it) }
    }

    SettingsFormScaffold(
        title = "Mis animales",
        onBack = onBack,
        snackbarHost = snackbarHost
    ) {

        LazyRow(
            contentPadding = PaddingValues(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(STATUS_FILTERS) { (key, label) ->
                FilterChip(
                    selected = (state.selectedStatus ?: "available") == key,
                    onClick = { viewModel.selectStatus(key) },
                    label = { Text(label) }
                )
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@SettingsFormScaffold
        }

        if (state.animals.isEmpty()) {
            Text(
                text = "No hay animales en este estado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp)
            )
            return@SettingsFormScaffold
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.animals.forEach { animal ->
                AnimalMiniCard(
                    name = animal.name,
                    species = animal.species,
                    locationName = animal.locationName,
                    profileImage = animal.profileImage,
                    onClick = { onAnimalClick(animal.id) }
                )
            }
        }
    }
}
