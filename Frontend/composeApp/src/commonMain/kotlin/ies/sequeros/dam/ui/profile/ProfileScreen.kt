package ies.sequeros.dam.ui.profile

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.components.profile.ProfileContent
import ies.sequeros.dam.ui.theme.PawpPurple
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(

    onBack: () -> Unit,
    onEditClick: () -> Unit,
    isOwnProfile: Boolean = true
) {

    val appViewModel: AppViewModel = koinViewModel()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text("Mi perfil") },
                navigationIcon = {

                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (currentUser == null) {

            Box(
                modifier         = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(color = PawpPurple)
            }
            return@Scaffold
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileContent(
                
                user         = currentUser!!,
                isOwnProfile = isOwnProfile,
                onEditClick  = onEditClick
            )
        }
    }
}