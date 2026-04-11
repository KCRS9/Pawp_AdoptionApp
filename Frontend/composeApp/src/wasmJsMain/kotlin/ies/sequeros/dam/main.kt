package ies.sequeros.dam

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import ies.sequeros.dam.di.appModule
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(application = {
            modules(
                appModule,
                module {
                    // En WasmJS, Settings usa localStorage del navegador (igual que JS)
                    single<Settings> { StorageSettings() }
                }
            )
        }) {
            App()
        }
    }
}
