import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization) // NUEVO: habilita @Serializable en las clases
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {

        // --- Dependencias comunes a TODAS las plataformas ---
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle y ViewModel
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navegación entre pantallas
            implementation(libs.navigation.compose)

            // Ktor — cliente HTTP
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)   // Serialización automática JSON
            implementation(libs.ktor.serialization.kotlinx.json)   // Formato JSON con kotlinx
            implementation(libs.ktor.client.auth)                  // Soporte Bearer token
            implementation(libs.ktor.client.logging)               // Ver peticiones en consola

            // Koin — inyección de dependencias
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Utilidades
            implementation(libs.kotlinx.serialization.json)   // Serialización JSON
            implementation(libs.kotlinx.datetime)             // Fechas multiplataforma
            implementation(libs.multiplatform.settings)       // Almacenamiento del token
            implementation(libs.multiplatform.settings.no.arg)

            // Imágenes
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Layouts responsivos (adapta la UI a móvil/tablet/escritorio)
            implementation(libs.material3.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)

            // Iconos extendidos de Material (más allá de los básicos)
            // compose.materialIconsExtended es provisto directamente por el plugin de Compose
            // Multiplatform y usa la versión correcta automáticamente
            implementation(compose.materialIconsExtended)

            implementation(libs.filekit.compose)
        }

        // --- Dependencias exclusivas de Android ---
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            // Motor HTTP para Android (usa OkHttp internamente)
            implementation(libs.ktor.client.okhttp)
            // Cifrado del token en Android (EncryptedSharedPreferences)
            implementation(libs.androidx.security.crypto)
        }

        // --- Dependencias exclusivas de Desktop (JVM) ---
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing) // Necesario para coroutines en Desktop
            // Motor HTTP para Desktop (también usa OkHttp)
            implementation(libs.ktor.client.okhttp)
        }

        // --- Dependencias exclusivas de Web (JavaScript) ---
        jsMain.dependencies {
            // Motor HTTP nativo del navegador
            implementation(libs.ktor.client.js)
        }

        // --- Tests comunes ---
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "ies.sequeros.dam"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ies.sequeros.dam"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("io.ktor:ktor-client-logging:3.3.3")
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "ies.sequeros.dam.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ies.sequeros.dam"
            packageVersion = "1.0.0"
        }
    }
}