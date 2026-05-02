package ies.sequeros.dam.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.components.profile.ProfileContent
import ies.sequeros.dam.ui.profile.ProfileViewModel
import ies.sequeros.dam.ui.theme.PawpPurple
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onAnimalClick: (String) -> Unit = {},
    onPostClick: (Int) -> Unit = {},
    onPublicationsClick: ((String, String) -> Unit)? = null
) {
    val viewModel: UserProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val profileViewModel: ProfileViewModel = koinViewModel()
    val profilePostsState by profileViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.load(userId)
            profileViewModel.loadPosts(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.user?.name ?: "Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading || state.user == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PawpPurple)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileContent(
                user                = state.user!!,
                isOwnProfile        = false,
                onEditClick         = onEditClick,
                favoriteAnimals     = state.favoriteAnimals,
                onAnimalClick       = onAnimalClick,
                posts               = profilePostsState.posts,
                onPostClick         = onPostClick,
                isLoadingPosts      = profilePostsState.isLoading,
                onPublicationsClick = onPublicationsClick
            )
        }
    }
}
