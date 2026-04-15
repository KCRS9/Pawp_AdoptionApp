package ies.sequeros.dam.ui.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ies.sequeros.dam.ui.appsettings.AppViewModel
import ies.sequeros.dam.ui.extensions.toRoleLabel
import ies.sequeros.dam.ui.extensions.toTitleCase
import ies.sequeros.dam.ui.theme.PawpPurple
import ies.sequeros.dam.ui.theme.PawpPurpleDark
import ies.sequeros.dam.ui.theme.PawpSurfaceDark
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {

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
                        .size(196.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector        = Icons.Filled.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier           = Modifier.size(196.dp),
                    tint               = PawpPurple
                )
            }

            Spacer(Modifier.height(16.dp))

            // Nombre con lápiz de edición al lado
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text  = user.name.toTitleCase(),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick  = onEditClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Edit,
                        contentDescription = "Editar perfil",
                        tint               = PawpPurple,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

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
            // Provincia
            user.locationName?.let { province ->

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = PawpPurple.copy(alpha = 0.52f)
                ){
                    Text(
                        text = province,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Descripción — recuadro con fondo adaptado al tema
            val descriptionBg = if (isSystemInDarkTheme()) PawpSurfaceDark else Color(0xFFF0F0F0)
            Surface(
                shape    = MaterialTheme.shapes.medium,
                color    = descriptionBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                if (!user.description.isNullOrBlank()) {
                    Text(
                        text     = user.description,
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Placeholder en gris cursiva hasta que el usuario complete su bio
                    Text(
                        text      = "Cuéntanos algo de ti y qué es lo que más te gusta de los animales...",
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontStyle = FontStyle.Italic,
                        modifier  = Modifier.padding(16.dp)
                    )
                }
            }


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