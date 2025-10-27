package com.example.monkibox.usuario

import java.util.UUID

data class Purchase(
    val id: String = UUID.randomUUID().toString(),
    val items: List<CartItem>, // La lista de artículos que compró
    val subtotal: Double,
    val shipping: Double,
    val taxes: Double,
    val total: Double,
    val date: Long = System.currentTimeMillis() // La fecha de la compra
)