package com.example.monkibox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkibox.admin.Product
import com.example.monkibox.admin.ProductStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

// Usamos AndroidViewModel para poder acceder al 'context' de forma segura
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    // 1. El contexto de la aplicación
    private val context = application.applicationContext

    // 2. El "Estado" (La lista de productos)
    // _productList es PRIVADO y editable (Mutable)
    private val _productList = MutableStateFlow<List<Product>>(emptyList())

    // productList es PÚBLICO y de solo lectura (StateFlow)
    // La UI "observará" este flow
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()

    // 3. Bloque de inicialización: Carga los productos en cuanto se crea el ViewModel
    init {
        loadProducts()
    }

    // 4. Las funciones de lógica (se ejecutan en hilos de fondo)

    fun loadProducts() {
        // viewModelScope.launch inicia una Corutina
        // Dispatchers.IO es el "hilo de fondo" optimizado para Disco/Red
        viewModelScope.launch(Dispatchers.IO) {
            // Esta llamada "lenta" ahora ocurre en segundo plano
            _productList.value = ProductStorage.getAllProducts(context)
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            ProductStorage.addProduct(context, product)
            // Recargamos la lista después de añadir
            loadProducts()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            ProductStorage.updateProduct(context, product)
            loadProducts()
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ProductStorage.deleteProduct(context, productId)
            loadProducts()
        }
    }
}