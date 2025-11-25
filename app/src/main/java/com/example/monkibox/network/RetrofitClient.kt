package com.example.monkibox.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.219.37.8:8080/"

    // Creamos la instancia PRINCIPAL de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 1. API de Productos
    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    // 2. API de Usuarios
    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    // 3. API EXTERNA (RandomUser)
    private const val BASE_URL_RANDOMUSER = "https://randomuser.me/"

    val randomUserApi: RandomUserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_RANDOMUSER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomUserApiService::class.java)
    }
}