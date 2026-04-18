package ies.sequeros.dam.ui.shelters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.shelter.ShelterCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProtectorasScreen(

    onShelterClick: (String) -> Unit = {}
) {
    val viewModel: ProtectorasViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        when {

            state.isLoading -> {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.shelters.isEmpty() && state.errorMessage == null -> {

                Text(
                    text = "No hay protectoras registradas",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            state.errorMessage != null -> {

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.errorMessage ?: "")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::loadShelters) { Text("Reintentar") }
                }
            }
            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(state.shelters) { shelter ->
                        ShelterCard(
                            shelter = shelter,
                            // TODO: sustituir por el nombre real cuando tengamos GetLocalityByIdUseCase
                            localityName = "Localidad ${shelter.location}",
                            onClick = { onShelterClick(shelter.id) }
                        )
                    }
                }
            }
        }
    }
}