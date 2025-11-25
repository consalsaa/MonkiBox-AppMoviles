package com.example.monkibox.dataclass

data class User(
    val id: Long? = null, // Puede ser nulo al registrarse
    val email: String,
    val password: String,
    val role: String? = "USER" // Por defecto es USER, pero puede ser ADMIN
)