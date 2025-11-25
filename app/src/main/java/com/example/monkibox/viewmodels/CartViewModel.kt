package com.example.monkibox.viewmodels
import com.example.monkibox.dataclass.Product
import com.example.monkibox.dataclass.CartItem
import com.example.monkibox.dataclass.Purchase
import com.example.monkibox.storage.HistoryStorage
import com.example.monkibox.storage.CartStorage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. Data class para los totales
data class CartTotals(
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val taxes: Double = 0.0,
    val total: Double = 0.0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // --- ESTADOS ---
    // Estado para la lista de artículos
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Estado para los totales (calculado automáticamente)
    val cartTotals: StateFlow<CartTotals> = _cartItems.map { items ->
        calculateTotals(items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartTotals()
    )

    // --- INICIALIZACIÓN ---
    init {
        loadCart()
    }

    fun loadCart() { // ¡Ahora es pública!
        viewModelScope.launch(Dispatchers.IO) {
            _cartItems.value = CartStorage.getAllCartItems(context)
        }
    }

    private fun saveCart() {
        viewModelScope.launch(Dispatchers.IO) {
            CartStorage.saveCartItems(context, _cartItems.value)
        }
    }

    // --- CÁLCULO DE TOTALES ---
    private fun calculateTotals(items: List<CartItem>): CartTotals {
        val subtotal = items.sumOf { it.product.price * it.quantity }

        // Lógica de ejemplo (puedes cambiarla)
        val shipping = if (subtotal > 0) 50.0 else 0.0 // Envío fijo
        val taxes = subtotal * 0.19 // 19% IVA
        val total = subtotal + shipping + taxes

        return CartTotals(subtotal, shipping, taxes, total)
    }

    // --- ACCIONES DEL CARRITO ---
    fun addItem(product: Product, quantity: Int) {
        val currentList = _cartItems.value
        val existingItem = currentList.find { it.product.id == product.id }

        val newList: List<CartItem>

        if (existingItem != null) {
            val itemIndex = currentList.indexOf(existingItem)

            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + quantity
            )
            // Si ya existe, actualiza la cantidad
            newList = currentList.toMutableList().apply {
                this[itemIndex] = updatedItem
            }
        } else {
            // Si no, lo añade como nuevo
            newList = currentList + CartItem(product = product, quantity = quantity)
        }

        _cartItems.value = newList // Asignamos la lista nueva al StateFlow
        saveCart() // Guarda los cambios
    }

    fun removeItem(cartItemId: String) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.id == cartItemId }
        _cartItems.value = currentList
        saveCart()
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) { // Si la cantidad es 0 o menos, elimínalo
            removeItem(cartItemId)
            return
        }

        val currentList = _cartItems.value
        val itemIndex = currentList.indexOfFirst { it.id == cartItemId }
        if (itemIndex != -1) {
            val itemToUpdate = currentList[itemIndex]
            val updatedItem = itemToUpdate.copy(quantity = newQuantity)
            val newList = currentList.toMutableList().apply {
                this[itemIndex] = updatedItem
            }
            _cartItems.value = newList
            saveCart()
        }
    }

    // --- ACCIÓN DE COMPRA ---
    fun checkout(onSuccess: () -> Unit) {
        if (_cartItems.value.isEmpty()) return // No se puede comprar un carrito vacío

        val currentTotals = cartTotals.value

        val purchase = Purchase(
            items = _cartItems.value,
            subtotal = currentTotals.subtotal,
            shipping = currentTotals.shipping,
            taxes = currentTotals.taxes,
            total = currentTotals.total
        )

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Guarda la compra en el historial
            HistoryStorage.addPurchase(context, purchase)

            // 2. Limpia el carrito
            _cartItems.value = emptyList()
            CartStorage.saveCartItems(context, emptyList())
        }

        // 3. Notifica a la UI que fue exitoso
        onSuccess()
    }
}