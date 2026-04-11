package ies.sequeros.dam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import ies.sequeros.dam.di.appModule
import org.koin.compose.KoinApplication
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(application = {
                modules(
                    appModule,
                    module {
                        single<Settings> {
                            SharedPreferencesSettings(
                                getSharedPreferences("pawp_prefs", MODE_PRIVATE)
                            )
                        }
                    }

                )
            }){
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}