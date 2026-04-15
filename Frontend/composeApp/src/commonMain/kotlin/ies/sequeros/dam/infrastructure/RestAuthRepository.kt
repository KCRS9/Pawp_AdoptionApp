package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IAuthRepository
import ies.sequeros.dam.infrastructure.dtos.ErrorResponseDto
import ies.sequeros.dam.infrastructure.dtos.LoginResponseDto
import ies.sequeros.dam.infrastructure.dtos.RegisterRequestDto
import ies.sequeros.dam.infrastructure.dtos.RegisterResponseDto
import ies.sequeros.dam.infrastructure.mappers.toDomain
import ies.sequeros.dam.infrastructure.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class RestAuthRepository(

    private val client: HttpClient,
    private val tokenStorage: TokenStorage,
    private val baseUrl: String
) : IAuthRepository {

    override suspend fun register(

        name: String,
        email: String,
        password: String,
        location: Int,
        role: String
    ): User {

        println("LOG [RestAuthRepository]: Iniciando registro para $email")

        val response = client.post("$baseUrl/users/signup/") {

            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(name, email, password, location, role))
        }

        if (!response.status.isSuccess()) {

            val error = parseError(response)
            println("LOG [RestAuthRepository]: Error en registro → ${error.message}")
            throw error
        }

        println("LOG [RestAuthRepository]: Registro exitoso para $email")

        val dto = response.body<RegisterResponseDto>()
        return dto.toDomain(name, email, location, role)
    }

    override suspend fun login(

        email: String,
        password: String
    ) {
        println("LOG [RestAuthRepository]: Iniciando login para $email")

        val response = client.post("$baseUrl/users/login/") {
            // Es necesario usar FormDataContent, ya que encapsula Parameters en un formato que todos los motores de Ktor entienden (JVM, JS, WasmJS).
            setBody(FormDataContent(
                Parameters.build {
                    append("username", email)   // FastAPI llama "username" al campo de identificación
                    append("password", password)
            }))
        }

        if (!response.status.isSuccess()) {

            val error = parseError(response)
            println("LOG [RestAuthRepository]: Error en login → ${error.message}")
            throw error
        }

        val dto = response.body<LoginResponseDto>()
        tokenStorage.saveToken(dto.accessToken)
        println("LOG [RestAuthRepository]: Login exitoso. Token guardado.")
    }

    private suspend fun parseError(
        response: HttpResponse

    ): Exception {

        return try {

            val error = response.body<ErrorResponseDto>()
            Exception(error.detail)

        } catch (e: Exception) {
            Exception("Error del servidor (${response.status.value})")
        }
    }
}