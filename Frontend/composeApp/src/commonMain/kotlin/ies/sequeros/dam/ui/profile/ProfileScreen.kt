package ies.sequeros.dam.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.toRoleLabel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {

    val appViewModel: AppViewModel = koinViewModel()
    val currentUser by appViewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (currentUser == null) {
            Box(
                modifier           = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment   = Alignment.Center
            ) {
                CircularProgressIndicator(color = PawpPurple)
            }
            return@Scaffold
        }

        val user = currentUser!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Foto de perfil
            if (user.profileImage != null) {
                AsyncImage(
                    model              = user.profileImage,
                    contentDescription = "Foto de perfil",
                    modifier           = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector        = Icons.Filled.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier           = Modifier.size(96.dp),
                    tint               = PawpPurple
                )
            }

            Spacer(Modifier.height(16.dp))

            // Nombre
            Text(
                text  = user.name,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(6.dp))

            // Etiqueta de rol
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = PawpPurple.copy(alpha = 0.12f)
            ) {
                Text(
                    text     = user.role.toRoleLabel(),
                    style    = MaterialTheme.typography.labelMedium,
                    color    = PawpPurple,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Descripción — solo si el backend ya la devuelve
            if (!user.description.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text     = user.description,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Favoritos — placeholder
            ProfileSection(title = "Favoritos") {
                Text(
                    text  = "Aquí aparecerán los animales en un carrusel horizontal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // Publicaciones — placeholder
            ProfileSection(title = "Publicaciones") {
                Text(
                    text  = "Aquí aparecerán las publicaciones propias en un carrusel vertical",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}