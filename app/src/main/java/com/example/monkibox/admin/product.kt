package com.example.monkibox.admin

import java.util.UUID

// Esta data class define la estructura de un Producto
data class Product(
    val id: String = UUID.randomUUID().toString().substring(0, 8), // ID único corto
    val name: String,
    val price: Double,
    val stock: Int,
    val description: String = "",
    val imageUrl: String = ""
)