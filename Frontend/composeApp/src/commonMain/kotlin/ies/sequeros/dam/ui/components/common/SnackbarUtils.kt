package ies.sequeros.dam.ui.components.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SNACKBAR_DURATION_MS = 1500L

suspend fun SnackbarHostState.showBrief(message: String) = coroutineScope {
    val job = launch {
        showSnackbar(message, duration = SnackbarDuration.Indefinite)
    }
    delay(SNACKBAR_DURATION_MS)
    currentSnackbarData?.dismiss()
}
