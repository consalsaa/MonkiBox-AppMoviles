package com.example.monkibox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkibox.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Clase auxiliar para unir el usuario de la API con un texto falso
data class FakeReview(
    val name: String,
    val country: String,
    val photoUrl: String,
    val comment: String,
    val stars: Int
)

class AboutViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<FakeReview>>(emptyList())
    val reviews: StateFlow<List<FakeReview>> = _reviews

    // Lista de comentarios predefinidos para asignar aleatoriamente
    private val possibleComments = listOf(
        "¡Me encantó la BlindBox! Llegó súper rápido.",
        "Productos muy originales, a mi hermana le fascinó.",
        "Excelente calidad, volveré a comprar seguro.",
        "El envío tardó un poco, pero valió la pena.",
        "Increíble experiencia, muy recomendado.",
        "El peluche es hermoso, tal como en la descripción. 🤩"
    )

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            try {
                // Pedimos 5 usuarios a la API
                val response = RetrofitClient.randomUserApi.getRandomUsers(5)

                // Transformamos los datos de la API en nuestras "Reseñas Falsas"
                val fakeReviewsList = response.results.map { user ->
                    FakeReview(
                        name = "${user.name.first} ${user.name.last}",
                        country = user.location.country,
                        photoUrl = user.picture.medium,
                        comment = possibleComments.random(), // Elegimos un comentario al azar
                        stars = (4..5).random() // Damos 4 o 5 estrellas al azar
                    )
                }

                _reviews.value = fakeReviewsList

            } catch (e: Exception) {
                e.printStackTrace() // Si falla (ej: sin internet), la lista queda vacía
            }
        }
    }
}