package ies.sequeros.dam.infrastructure.ktor

import ies.sequeros.dam.infrastructure.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(

    tokenStorage: TokenStorage,
    onSessionExpired: () -> Unit = {}
): HttpClient {

    return HttpClient {

        // Cabecera Content-Type: application/json por defecto en todas las peticiones.
        // El login la sobreescribe a form-urlencoded (ver RestAuthRepository).
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        // Logging de todas las peticiones y respuestas.
        // Cambia LogLevel.ALL a LogLevel.NONE para producción.
        install(Logging) {

            logger = object : Logger {
                override fun log(message: String) {
                    println("LOG [Ktor]: $message")
                }
            }
            level = LogLevel.ALL
        }

        // Serialización automática JSON ↔ Kotlin data classes
        install(ContentNegotiation) {
            json(Json {

                ignoreUnknownKeys = true    // Si el backend añade campos nuevos, no falla
                prettyPrint = true
                isLenient = true
            })
        }

        // Plugin de autenticación Bearer.
        // Añade automáticamente "Authorization: Bearer <token>" en cada petición.
        install(Auth) {
            bearer {
                // Ktor llama a esto antes de cada petición protegida
                loadTokens {

                    val token = tokenStorage.getAccessToken()
                    if (token != null) {
                        println("LOG [Ktor]: Adjuntando token a la petición")
                        // BearerTokens(accessToken, refreshToken)
                        // Como no tenemos refresh token, pasamos el mismo dos veces
                        BearerTokens(token, token)
                    } else {
                        null
                    }
                }

                // Ktor llama a esto cuando el servidor devuelve 401 (token inválido/expirado)
                refreshTokens {

                    println("LOG [Ktor]: Token rechazado (401). Cerrando sesión.")
                    tokenStorage.clear()
                    onSessionExpired()
                    null    // null = no se pudo renovar, Ktor cancelará la petición
                }
            }
        }

        // Si el servidor no responde en 15 segundos, la petición falla
        install(HttpTimeout) {

            requestTimeoutMillis = 15_000
            socketTimeoutMillis  = 15_000
        }
    }
}