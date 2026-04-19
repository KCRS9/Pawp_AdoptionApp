package ies.sequeros.dam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import pawp_adoption.composeapp.generated.resources.Poppins_extrabold
import pawp_adoption.composeapp.generated.resources.Res
import pawp_adoption.composeapp.generated.resources.poppins_medium
import pawp_adoption.composeapp.generated.resources.poppins_regular
import pawp_adoption.composeapp.generated.resources.poppins_semibold

/*
Esquema de colores para el Modo CLARO
Fondo blanco, texto oscuro, acentos morados
*/
private val LightColorScheme = lightColorScheme(
    primary          = PawpPurple,       // Botones, checkboxes, sliders
    onPrimary        = Color.White,      // Texto/icono sobre botones primarios
    secondary        = PawpPurpleLight,  // Elementos secundarios
    onSecondary      = Color.White,
    background       = Color.White,      // Fondo general
    onBackground     = PawpTextDark,     // Texto sobre fondo
    surface          = Color.White,      // Cards, sheets, dialogs
    onSurface        = PawpTextDark,     // Texto sobre cards
    surfaceVariant   = Color(0xFFF5F2F8), // Fondo de chips, inputs
    onSurfaceVariant = PawpTextDark,
)

/*
Esquema de colores para el Modo Oscuro
Fondo púrpura oscuro, texto blanco, acentos morados
*/
private val DarkColorScheme = darkColorScheme(
    primary          = PawpPurple,
    onPrimary        = Color.White,
    secondary        = PawpPurpleLight,
    onSecondary      = Color.White,
    background       = PawpPurpleDark,   // Fondo oscuro
    onBackground     = Color.White,
    surface          = PawpSurfaceDark,  // Cards en modo oscuro
    onSurface        = Color.White,
    surfaceVariant   = Color(0xFF4A4060),
    onSurfaceVariant = Color.White,
)

@Composable
fun PawpTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val poppins = FontFamily(
        Font(Res.font.poppins_regular,   FontWeight.Normal),
        Font(Res.font.poppins_medium,    FontWeight.Medium),
        Font(Res.font.poppins_semibold,  FontWeight.SemiBold),
        Font(Res.font.Poppins_extrabold, FontWeight.ExtraBold)
    )

    val typography = Typography(
        // Títulos de pantalla: "Iniciar Sesión", "Crear Cuenta" (SemiBold 32sp)
        displaySmall = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize   = TitleFontSize
        ),
        // Título de la card PAWP y encabezados destacados (ExtraBold 32sp)
        headlineLarge = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = TitleFontSize
        ),
        // Subtítulos de sección (SemiBold 17sp)
        headlineSmall = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp
        ),
        // Texto de cuerpo con peso medio (Medium 16sp)
        bodyLarge = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            fontSize   = BodyFontSize
        ),
        // Texto de cuerpo principal (Regular 16sp)
        bodyMedium = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize   = BodyFontSize
        ),
        // Labels y texto pequeño (Regular 14sp)
        bodySmall = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize   = SmallFontSize
        ),
        // Texto de botones (SemiBold 16sp)
        labelLarge = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize   = BodyFontSize
        ),
        // Labels de BottomNavigation (Regular 12sp)
        labelSmall = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize   = 12.sp
        )
    )

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content
    )
}
/** Fondo gris para recuadros de descripción/datos según el tema actual */
@Composable
fun pawpSurfaceColor() = if (androidx.compose.foundation.isSystemInDarkTheme()) PawpSurfaceDark else PawpSurfaceLight

/** Color de texto para recuadros de descripción/datos según el tema actual */
@Composable
fun pawpOnSurfaceTextColor() = if (androidx.compose.foundation.isSystemInDarkTheme()) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black

@Composable
fun loginTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    // Fondo del campo
    unfocusedContainerColor = PawpFieldBg.copy(alpha = 0.25f),
    focusedContainerColor   = PawpFieldBg.copy(alpha = 0.35f),
    // Texto que escribe el usuario
    unfocusedTextColor      = Color.White,
    focusedTextColor        = Color.White,
    // Label ("Email", "Contraseña")
    unfocusedLabelColor     = Color.White.copy(alpha = 0.8f),
    focusedLabelColor       = Color.White,
    // Borde del campo
    unfocusedBorderColor    = Color.White.copy(alpha = 0.5f),
    focusedBorderColor      = Color.White,
    // Cursor de escritura
    cursorColor             = Color.White,
    // Color del borde cuando hay error
    errorBorderColor        = Color(0xFFFF8A80),
    errorLabelColor         = Color(0xFFFF8A80),
    errorTextColor          = Color.White,
    errorSupportingTextColor = Color(0xFFFF8A80),
)

@Composable
fun pawpTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = PawpFieldBg.copy(alpha = 0.40f),
    focusedContainerColor   = PawpFieldBg.copy(alpha = 0.55f),
    focusedBorderColor      = PawpPurple,
    unfocusedBorderColor    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    focusedLabelColor       = PawpPurple,
)