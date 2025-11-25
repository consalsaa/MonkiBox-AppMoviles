package com.example.monkibox.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.monkibox.dataclass.Product

interface ProductApiService {

    // Obtener la lista
    @GET("/api/products")
    suspend fun getAllProducts(): List<Product>

    // Crear un producto
    @POST("/api/products")
    suspend fun createProduct(@Body product: Product): Product
}