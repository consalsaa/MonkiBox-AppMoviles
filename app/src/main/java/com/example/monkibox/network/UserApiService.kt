package com.example.monkibox.network

import com.example.monkibox.dataclass.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {

    // Registrarse
    // Usamos Response<User> para poder leer los códigos de error (ej: 400 si el correo existe)
    @POST("/api/users/register")
    suspend fun register(@Body user: User): Response<User>

    // Iniciar Sesión
    // Si el login es correcto, devuelve el User con sus datos (incluido el rol)
    @POST("/api/users/login")
    suspend fun login(@Body user: User): Response<User>

    // Obtener lista de usuarios (Para el admin)
    @GET("/api/users")
    suspend fun getAllUsers(): List<User>
}