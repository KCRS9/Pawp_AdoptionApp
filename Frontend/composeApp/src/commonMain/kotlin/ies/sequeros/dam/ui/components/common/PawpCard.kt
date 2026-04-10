package ies.sequeros.dam.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ies.sequeros.dam.ui.theme.PawpPurple
import org.jetbrains.compose.resources.painterResource
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.logo_pawp

/**
 * Tarjeta de marca de Pawp.
 *
 * @param showImage  true  → texto izquierda + logo derecha sobresaliendo por abajo
 *                   false → solo texto centrado (usado en Login)
 */
@Composable
fun PawpCard(
    modifier: Modifier = Modifier,
    showImage: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(PawpPurple)
                .padding(10.dp)
        ) {
            PawpCardCircles()

            if (showImage) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.55f)
                        .padding(start = 20.dp, ),  // deja espacio para la imagen a la derecha
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text  = "PAWP",
                        // displaySmall → Poppins SemiBold 32sp (definido en Theme.kt)
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Text(
                        text  = "Invita un amigo animal a casa",
                        // bodyMedium → Poppins Regular 16sp
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            } else {
                // Sin imagen: texto centrado
                Column(
                    modifier            = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text  = "PAWP",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Text(
                        text  = "Invita un amigo animal a casa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        // Imagen fuera del Box con clip → puede sobresalir 20dp por debajo de la card
        // offset(y = 20.dp) empuja la imagen hacia abajo, creando el efecto de "salir" de la tarjeta
        if (showImage) {
            Image(
                painter            = painterResource(Res.drawable.logo_pawp),
                contentDescription = "Logo Pawp",
                modifier           = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp)
                    .offset(y = 8.dp)
            )
        }
    }
}

/** 5 círculos decorativos con los colores de la paleta Pawp */
@Composable
private fun PawpCardCircles() {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1 — El más grande (60dp), inferior izquierda → #E9D5CA (melocotón)
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-12).dp, y = 12.dp)
                .background(Color(0xFFB6ACC5).copy(alpha = 0.80f), CircleShape)
        )

        // 2 — Mediano (40dp), superior derecha → #B6ACC5 (morado claro)
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopEnd)
                .offset(x = 12.dp, y = (-12).dp)
                .background(Color(0xFFE9D5CA).copy(alpha = 0.75f), CircleShape)
        )

        // 3 — Pequeño (12dp), superior izquierda → #D4B4C8 (rosa empolvado)
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.TopStart)
                .offset(x = 6.dp, y = 6.dp)
                .background(Color(0xFFD4B4C8).copy(alpha = 0.85f), CircleShape)
        )

        // 4 — Extra pequeño (20dp), centro-derecha ligeramente arriba → #F0CBBE (salmón claro)
        Box(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 4.dp, y = (-16).dp)
                .background(Color(0xFFF0CBBE).copy(alpha = 0.70f), CircleShape)
        )

        // 5 — Extra pequeño (16dp), inferior derecha → #C5B4D9 (lavanda suave)
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-28).dp, y = (-8).dp)
                .background(Color(0xFFC5B4D9).copy(alpha = 0.75f), CircleShape)
        )
    }
}
