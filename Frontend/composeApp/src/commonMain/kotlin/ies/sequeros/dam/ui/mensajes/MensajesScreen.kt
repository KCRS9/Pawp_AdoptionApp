package ies.sequeros.dam.ui.mensajes

import ies.sequeros.dam.domain.models.Message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.components.common.showBrief
import ies.sequeros.dam.ui.shelters.ProtectorasViewModel
import ies.sequeros.dam.ui.theme.PawpPurple
import org.koin.compose.viewmodel.koinViewModel

enum class MensajesDestination {
    MAIN,
    SELECT_SHELTER,
    CONVERSATION
}

@Composable
fun MensajesScreen() {
    val viewModel: MessagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var destination by remember { mutableStateOf(MensajesDestination.MAIN) }
    var selectedShelterId by remember { mutableStateOf(0) }
    var selectedShelterName by remember { mutableStateOf("") }

    when (destination) {
        MensajesDestination.MAIN -> {
            MessagesListContent(
                onNewMessageClick = {
                    destination = MensajesDestination.SELECT_SHELTER
                },
                onMessageClick = { message ->
                    selectedShelterId = message.recipientId
                    selectedShelterName = message.recipientName
                    destination = MensajesDestination.CONVERSATION
                }
            )
        }

        MensajesDestination.SELECT_SHELTER -> {
            SelectShelterContent(
                onBack = {
                    destination = MensajesDestination.MAIN
                },
                onShelterSelected = { shelterId, shelterName ->
                    selectedShelterId = shelterId
                    selectedShelterName = shelterName
                    destination = MensajesDestination.CONVERSATION
                }
            )
        }

        MensajesDestination.CONVERSATION -> {
            ConversationScreen(
                shelterName = selectedShelterName,
                shelterId = selectedShelterId,
                onBack = {
                    destination = MensajesDestination.MAIN
                }
            )
        }

    }
}

@Composable
private fun MessagesListContent(
    onNewMessageClick: () -> Unit,
    onMessageClick: (Message) -> Unit
) {
    val viewModel: MessagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 12.dp),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
        ) {
            item {
                PawpCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }

            item {
                Text(
                    text = "Mensajes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            when {
                state.isLoading && state.messages.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PawpPurple)
                        }
                    }
                }

                state.messages.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay mensajes aún",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    items(state.messages) { message ->
                        MessageItem(
                            message = message,
                            onClick = {
                                onMessageClick(message)
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewMessageClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PawpPurple
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Nuevo mensaje",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectShelterContent(
    onBack: () -> Unit,
    onShelterSelected: (Int, String) -> Unit
) {
    val viewModel: ProtectorasViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    val filteredShelters = state.shelters.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar protectora") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
            ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PawpCard(
                        showImage = false,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PawpPurple)
                    }
                }
            } else if (filteredShelters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay protectoras disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredShelters) { shelter ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                        onClick = {
                            onShelterSelected(shelter.id.hashCode(), shelter.name)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = shelter.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "${shelter.locationName ?: "Sin localidad"} | ${shelter.animalsAvailable} animales",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SendMessageContent(
    shelterName: String,
    shelterId: Int,
    onBack: () -> Unit
) {
    val viewModel: MessagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHost.showBrief(it)
            viewModel.clearSuccessMessage()
            messageText = ""
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHost.showBrief(it)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shelterName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            PawpCard(
                showImage = false,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = shelterName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text("Mensaje") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 5
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessageToShelter(shelterId, shelterName, messageText)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && messageText.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text("ENVIAR")
            }
            }
        }
    }
}

