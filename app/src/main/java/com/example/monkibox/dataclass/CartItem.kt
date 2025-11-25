package com.example.monkibox.dataclass

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(), // ID único del *artículo en el carrito*
    val product: Product, // Guardamos el producto completo
    var quantity: Int
)