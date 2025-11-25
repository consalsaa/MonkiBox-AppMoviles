package com.example.monkibox.dataclass

// Esta data class define la estructura de un Producto
data class Product(
    val id: Long? = null,
    val name: String,
    val price: Double,
    val stock: Int,
    val description: String = "",
    val imageUrl: String = ""
)