package ies.sequeros.dam.ui.adoptions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

private val STATUS_FILTERS = listOf(
    null        to "Todas",
    "pending"   to "Pendientes",
    "reviewing" to "En revisión",
    "approved"  to "Aprobadas",
    "rejected"  to "Rechazadas",
    "completed" to "Completadas"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesProtectoraScreen(
    onBack: () -> Unit,
    onAdoptionClick: (Int) -> Unit
) {
    val viewModel: SolicitudesProtectoraViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    val filtered = if (selectedStatus == null) state.adoptions
                   else state.adoptions.filter { it.status == selectedStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes recibidas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            PullToRefreshBox(
                isRefreshing = state.isLoading && state.adoptions.isNotEmpty(),
                onRefresh = { viewModel.load() },
                modifier = Modifier.widthIn(max = 480.dp).fillMaxSize()
            ) {
                when {
                    state.isLoading && state.adoptions.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        Column {
                            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                                items(STATUS_FILTERS) { (status, label) ->
                                    FilterChip(
                                        selected = selectedStatus == status,
                                        onClick = { selectedStatus = status },
                                        label = { Text(label) },
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }

                            if (filtered.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        if (selectedStatus == null) "No hay solicitudes recibidas"
                                        else "No hay solicitudes con este estado",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                                    items(filtered, key = { it.id }) { adoption ->
                                        AdoptionListItem(
                                            imageUrl = adoption.animalImage,
                                            title = adoption.animalName,
                                            subtitle = adoption.userName,
                                            status = adoption.status,
                                            onClick = { onAdoptionClick(adoption.id) },
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
