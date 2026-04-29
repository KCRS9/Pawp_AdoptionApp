package ies.sequeros.dam.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.common.PawpCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    onAnimalClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val viewModel: SocialViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val appViewModel: AppViewModel = koinViewModel()
    val sessionVersion by appViewModel.sessionVersion.collectAsStateWithLifecycle()
    LaunchedEffect(sessionVersion) { viewModel.refresh() }

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.posts.isNotEmpty(),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    PawpCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }

                when {
                    state.isLoading && state.posts.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }

                    state.posts.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aún no hay publicaciones.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    else -> {
                        items(state.posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                onLikeClick = { viewModel.toggleLike(post.id) },
                                onUserClick = onUserClick,
                                onAnimalClick = if (post.animalId != null) onAnimalClick else null,
                                modifier = Modifier.padding(horizontal = 12.dp)
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
