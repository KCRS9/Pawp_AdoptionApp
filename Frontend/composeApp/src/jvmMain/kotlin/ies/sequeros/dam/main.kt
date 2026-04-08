package ies.sequeros.dam

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "pawp_adoption",
    ) {
        App()
    }
}