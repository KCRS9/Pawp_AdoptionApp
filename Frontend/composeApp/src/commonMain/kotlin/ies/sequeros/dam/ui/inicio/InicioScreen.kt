package ies.sequeros.dam.ui.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.AnimalMiniCard
import ies.sequeros.dam.ui.components.common.PawpCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.icon_cat
import pawp_adoption.composeapp.generated.resources.icon_dog
import pawp_adoption.composeapp.generated.resources.icon_hamster
import pawp_adoption.composeapp.generated.resources.icon_rabbit
import pawp_adoption.composeapp.generated.resources.icon_turtle

private data class SpeciesFilter(val label: String, val key: String?, val icon: DrawableResource?)

private val SPECIES_FILTERS = listOf(
    SpeciesFilter("Todos", null, null),
    SpeciesFilter("Perros", "Perro", Res.drawable.icon_dog),
    SpeciesFilter("Gatos", "Gato", Res.drawable.icon_cat),
    SpeciesFilter("Conejos", "Conejo", Res.drawable.icon_rabbit),
    SpeciesFilter("Reptiles", "Reptil", Res.drawable.icon_turtle),
    SpeciesFilter("Otros", "Otro", Res.drawable.icon_hamster),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(onAnimalClick: (String) -> Unit = {}) {
    val viewModel: InicioViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val reachedEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= info.totalItemsCount - 3 && info.totalItemsCount > 0
        }
    }
    LaunchedEffect(reachedEnd) {
        if (reachedEnd) viewModel.loadMore()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxSize()
        ) {
            PawpCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SPECIES_FILTERS) { filter ->
                    FilterChip(
                        selected = state.selectedSpecies == filter.key,
                        onClick = { viewModel.selectSpecies(filter.key) },
                        label = { Text(filter.label) },
                        leadingIcon = filter.icon?.let { res ->
                            {
                                Image(
                                    painter = painterResource(res),
                                    contentDescription = filter.label,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading && state.animals.isNotEmpty(),
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    state.isLoading && state.animals.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    state.animals.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No hay animales disponibles.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.animals, key = { it.id }) { animal ->
                                AnimalMiniCard(
                                    name = animal.name,
                                    species = animal.species,
                                    gender = animal.gender,
                                    locationName = animal.locationName,
                                    profileImage = animal.profileImage,
                                    onClick = { onAnimalClick(animal.id) }
                                )
                            }
                            if (state.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
