package ies.sequeros.dam.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.PawpCard
import ies.sequeros.dam.ui.components.common.showBrief
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    onBack: () -> Unit,
    onAnimalClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val viewModel: PostDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    val appViewModel: AppViewModel = koinViewModel()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(postId) { viewModel.load(postId) }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBack()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHost.showBrief(it)
            viewModel.onErrorHandled()
        }
    }

    val canDelete = currentUser != null &&
        (currentUser!!.role == "admin" || currentUser!!.id == state.post?.userId)

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Publicación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (canDelete) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Eliminar publicación", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    viewModel.requestDeletePost()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {

            // scroll principal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        PawpCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }

                    state.post?.let { post ->
                        item {
                            PostCard(
                                post = post,
                                onLikeClick = { viewModel.toggleLike() },
                                onUserClick = onUserClick,
                                onAnimalClick = if (post.animalId != null) onAnimalClick else null,
                                showCommentBar = false,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Comentarios",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    if (state.isLoadingComments) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                        }
                    } else if (state.comments.isEmpty()) {
                        item {
                            Text(
                                text = "Sé el primero en comentar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        items(state.comments, key = { it.id }) { comment ->
                            val canDeleteComment = currentUser != null &&
                                (currentUser!!.role == "admin" || currentUser!!.id == comment.userId)
                            CommentItem(
                                comment = comment,
                                canDelete = canDeleteComment,
                                onDelete = { viewModel.deleteComment(comment.id) }
                            )
                        }
                    }
                }
            }

            // barra de escritura fija al pie
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.commentText,
                    onValueChange = viewModel::onCommentTextChange,
                    placeholder = { Text("Añadir un comentario...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = currentUser != null
                )
                IconButton(
                    onClick = viewModel::submitComment,
                    enabled = state.commentText.isNotBlank() && !state.isSubmitting
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, "Enviar comentario")
                    }
                }
            }
        }
    }

    if (state.showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeletePost,
            title   = { Text("¿Eliminar publicación?") },
            text    = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDeletePost) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeletePost) {
                    Text("Cancelar")
                }
            }
        )
    }
}
