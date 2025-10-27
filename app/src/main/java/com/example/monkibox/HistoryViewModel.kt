package com.example.monkibox
import com.example.monkibox.usuario.HistoryStorage
import com.example.monkibox.usuario.Purchase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // 1. Estado para la lista de compras
    private val _purchaseList = MutableStateFlow<List<Purchase>>(emptyList())
    val purchaseList: StateFlow<List<Purchase>> = _purchaseList.asStateFlow()

    // 2. Cargamos el historial al iniciar el ViewModel
    init {
        loadPurchaseHistory()
    }

    // 3. Función de carga en un hilo de fondo
    fun loadPurchaseHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _purchaseList.value = HistoryStorage.getAllPurchases(context)
        }
    }
}