package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.application.comandos.ChangeEmailCommand
import ies.sequeros.dam.application.comandos.ChangePasswordCommand
import ies.sequeros.dam.application.comandos.UpdateProfileCommand
import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.dtos.ErrorResponseDto
import ies.sequeros.dam.infrastructure.dtos.UserProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class RestUserRepository(

    private val client: HttpClient,
    private val baseUrl: String,
): IUserRepository {

    override suspend fun getCurrentUser(): User{

        println("LOG [RestUserRepository]: Solicitando perfil del usuario autenticado")

        val response: HttpResponse = client.get("$baseUrl/users/me")

        if(!response.status.isSuccess()){

            val detail = try {
                response.body<ErrorResponseDto>().detail

            }catch (e: Exception){
                "Error del servidor: ${e.message}"
            }

            println("LOG [RestUserRepository]: Error al obtener perfil → $detail")
            throw Exception(detail)
        }

        val dto = response.body<UserProfileDto>()

        println("LOG [RestUserRepository]: Perfil obtenido → ${dto.name} (${dto.role})")

        return User(

            id = dto.id,
            name = dto.name,
            email = dto.email,
            location = dto.location,
            role = dto.role,
            // Es necesario agregar la Url si no viene con ella
            profileImage = dto.profileImage?.let {
                if (it.startsWith("/")) "$baseUrl$it" else it
            },
            shelterId = dto.shelterId,
            description = dto.description
        )
    }

    override suspend fun updateProfile(command: UpdateProfileCommand){

        val body = buildJsonObject {
            command.name?.let { put("name", it) }
            command.locationId?.let { put("location", it) }
            command.description?.let { put("description", it) }
        }

        val response = client.patch ("$baseUrl/users/me"){

            contentType(ContentType.Application.Json)
            setBody(body)
        }

        if (!response.status.isSuccess()) {
            val detail = try { response.body<ErrorResponseDto>().detail }
            catch (e: Exception) { "Error al actualizar el perfil" }
            throw Exception(detail)
        }
    }

    override suspend fun updateAvatar(imageBytes: ByteArray, fileName: String) {

        val response = client.post("$baseUrl/users/me/avatar") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key   = "avatar",
                            value = imageBytes,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"avatar\"; filename=\"$fileName\""
                                )
                                append(
                                    HttpHeaders.ContentType,
                                    ContentType.Image.JPEG.toString())
                            }
                        )
                    }
                )
            )
        }

        if(!response.status.isSuccess()){

            val detail =
                try {
                    response.body<ErrorResponseDto>().detail
                }catch (e: Exception){
                    "Error al actualizar la foto ${e.message}"
                }

            throw Exception(detail)
        }
    }

    override suspend fun changePassword(command: ChangePasswordCommand) {

        val body = buildJsonObject {
            put("old_password", command.oldPassword)
            put("new_password", command.newPassword)
        }

        val response = client.patch("$baseUrl/users/me/password") {

            contentType(ContentType.Application.Json)
            setBody(body)
        }

        if (!response.status.isSuccess()) {

            val detail =
                try {
                    response.body<ErrorResponseDto>().detail }
                catch (e: Exception) {
                    "Error al cambiar la contraseña: ${e.message}"
                }

            throw Exception(detail)
        }
    }

    override suspend fun changeEmail(command: ChangeEmailCommand) {

        val body = buildJsonObject {
            put("new_email", command.newEmail)
            put("password", command.password)
        }

        val response = client.patch("$baseUrl/users/me/email") {

            contentType(ContentType.Application.Json)
            setBody(body)
        }

        if (!response.status.isSuccess()) {

            val detail =
                try {
                    response.body<ErrorResponseDto>().detail
                } catch (e: Exception) {
                    "Error al cambiar el correo. ${e.message}"
                }

            throw Exception(detail)
        }
    }

}