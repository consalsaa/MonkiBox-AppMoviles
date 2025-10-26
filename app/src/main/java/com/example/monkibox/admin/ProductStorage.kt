package com.example.monkibox.admin

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ProductStorage {

    private const val PREFERENCES_FILE_NAME = "MonkiBoxPrefs"
    private const val KEY_PRODUCTS = "product_list"

    // Creamos una instancia de GSON para convertir a/desde JSON
    private val gson = Gson()

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    // --- FUNCIÓN PRINCIPAL PARA LEER LA LISTA ---
    fun getAllProducts(context: Context): List<Product> {
        val prefs = getPreferences(context)
        val json = prefs.getString(KEY_PRODUCTS, null)

        // Si no hay nada guardado, devuelve una lista vacía
        if (json == null) {
            return emptyList()
        }

        // Si hay JSON, GSON lo convierte de nuevo a una List<Product>
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(json, type)
    }

    // --- FUNCIÓN PRINCIPAL PARA GUARDAR LA LISTA ---
    private fun saveProducts(context: Context, productList: List<Product>) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()

        // GSON convierte la lista de productos a un solo string JSON
        val json = gson.toJson(productList)

        editor.putString(KEY_PRODUCTS, json)
        editor.apply()
    }

    // --- Funciones de ayuda ---

    // Añade un nuevo producto a la lista existente
    fun addProduct(context: Context, product: Product) {
        val currentList = getAllProducts(context).toMutableList()
        currentList.add(product)
        saveProducts(context, currentList)
    }

    // Edita un producto existente
    fun updateProduct(context: Context, updatedProduct: Product) {
        val currentList = getAllProducts(context).toMutableList()
        // Buscamos el índice (posición) del producto viejo
        val index = currentList.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            // Si lo encontramos, lo reemplazamos en esa posición
            currentList[index] = updatedProduct
            saveProducts(context, currentList)
        }
    }

    // Elimina un producto por su ID
    fun deleteProduct(context: Context, productId: String) {
        val currentList = getAllProducts(context).toMutableList()
        // Removemos de la lista todos los que coincidan con ese ID
        currentList.removeAll { it.id == productId }
        saveProducts(context, currentList)
    }

    fun getProductCount(context: Context): Int {
        return getAllProducts(context).size
    }
}