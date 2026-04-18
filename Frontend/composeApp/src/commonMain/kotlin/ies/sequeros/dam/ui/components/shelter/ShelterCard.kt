package ies.sequeros.dam.ui.components.shelter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ies.sequeros.dam.domain.models.ShelterSummary


@Composable
fun ShelterCard(

    shelter: ShelterSummary,
    localityName: String,
    onClick: () -> Unit
) {
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(

            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo de la protectora o placeholder con la inicial
            if (shelter.profileImage != null) {

                AsyncImage(

                    model = shelter.profileImage,
                    contentDescription = shelter.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(

                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {

                    Box(contentAlignment = Alignment.Center) {

                        Text(
                            text = shelter.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column {

                Text(shelter.name, style = MaterialTheme.typography.titleMedium)

                Text(
                    localityName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}