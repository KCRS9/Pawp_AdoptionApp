package ies.sequeros.dam

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import ies.sequeros.dam.di.appModule
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import java.util.prefs.Preferences

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "pawp_adoption",
    ) {
        KoinApplication(application = {

            modules(
                appModule,
                module{
                    single<Settings>{
                        PreferencesSettings(Preferences.userRoot().node("pawp"))
                    }
                }
            )
        }){
            App()
        }

    }
}