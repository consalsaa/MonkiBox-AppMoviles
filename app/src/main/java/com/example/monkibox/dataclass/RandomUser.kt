package com.example.monkibox.dataclass

// La respuesta completa de la API
data class RandomUserResponse(
    val results: List<UserResult>
)

// Los datos del usuario
data class UserResult(
    val name: UserName,
    val picture: UserPicture,
    val location: UserLocation // Para poner el país
)

data class UserName(
    val first: String,
    val last: String
)

data class UserPicture(
    val medium: String // URL de la foto
)

data class UserLocation(
    val country: String
)