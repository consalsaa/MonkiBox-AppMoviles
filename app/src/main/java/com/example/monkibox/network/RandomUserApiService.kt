package com.example.monkibox.network

import com.example.monkibox.dataclass.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApiService {
    // Pediremos 5 usuarios aleatorios
    @GET("api/")
    suspend fun getRandomUsers(@Query("results") count: Int): RandomUserResponse
}