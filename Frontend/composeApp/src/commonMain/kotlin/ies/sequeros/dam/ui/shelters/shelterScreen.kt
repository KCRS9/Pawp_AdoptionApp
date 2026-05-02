package ies.sequeros.dam.ui.shelters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.components.shelter.ShelterCard
import ies.sequeros.dam.ui.theme.PawpPurple
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectorasScreen(

    onShelterClick: (String) -> Unit = {}
) {

    val viewModel: ProtectorasViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    // ModalBottomSheet: selector de zona
    if (showSheet) {

        ModalBottomSheet(

            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Selecciona una zona", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                // opcion "Todas las zonas"
                TextButton(

                    onClick = {
                        viewModel.selectLocation(null)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                    },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(

                        "Todas las zonas",
                        color = if (state.selectedLocation == null) PawpPurple
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                state.localities.forEach { locality ->
                    TextButton(
                        onClick = {
                            viewModel.selectLocation(locality)
                            scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            locality.name,
                            color = if (state.selectedLocation?.id == locality.id) PawpPurple
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

        PullToRefreshBox(

            isRefreshing = state.isLoading && state.shelters.isNotEmpty(),
            onRefresh = { viewModel.loadShelters() },
            modifier = Modifier.widthIn(max = 480.dp).fillMaxSize()
        ) {
            when {
                state.isLoading && state.shelters.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.errorMessage ?: "")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = viewModel::loadShelters) { Text("Reintentar") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            PawpCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                        }

                        // Chip de zona — abre el BottomSheet
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                FilterChip(
                                    selected = state.selectedLocation != null,
                                    onClick = { showSheet = true },
                                    label = {
                                        Text(state.selectedLocation?.name ?: "Todas las zonas")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }

                        if (state.shelters.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No hay protectoras en esta zona.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(state.shelters, key = { it.id }) { shelter ->
                                ShelterCard(
                                    shelter = shelter,
                                    onClick = { onShelterClick(shelter.id) },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}