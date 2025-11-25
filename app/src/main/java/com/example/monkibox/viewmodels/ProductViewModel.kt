package com.example.monkibox.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkibox.dataclass.Product
import com.example.monkibox.network.RetrofitClient // Asegúrate de importar tu cliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()

    init {
        loadProducts()
    }

    // 1. Cargar productos desde la API
    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // ¡Llamada a la NUBE! ☁️
                val productosDelServidor = RetrofitClient.productApi.getAllProducts()
                _productList.value = productosDelServidor
                Log.d("API", "Productos cargados: ${productosDelServidor.size}")
            } catch (e: Exception) {
                // Si falla, mostramos el error en la consola
                Log.e("API", "Error cargando productos: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // 2. Crear producto en la API
    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Enviamos el producto a Spring Boot
                RetrofitClient.productApi.createProduct(product)
                // Recargamos la lista para ver el nuevo producto
                loadProducts()
            } catch (e: Exception) {
                Log.e("API", "Error creando producto: ${e.message}")
            }
        }
    }

    // Nota: Por ahora comentaremos update y delete hasta que los implementemos en el backend
    /*
    fun updateProduct(product: Product) { ... }
    fun deleteProduct(productId: Long) { ... }
    */
}