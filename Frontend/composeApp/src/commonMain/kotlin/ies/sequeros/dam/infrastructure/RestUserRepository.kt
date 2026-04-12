package ies.sequeros.dam.infrastructure

import ies.sequeros.dam.domain.models.User
import ies.sequeros.dam.domain.repositories.IUserRepository
import ies.sequeros.dam.infrastructure.dtos.ErrorResponseDto
import ies.sequeros.dam.infrastructure.dtos.UserProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

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
            profileImage = dto.profileImage,
            shelterId = dto.shelterId
        )
    }
}