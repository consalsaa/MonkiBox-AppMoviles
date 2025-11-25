package com.example.monkibox.storage

import android.content.Context
import com.example.monkibox.dataclass.Purchase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HistoryStorage {
    private const val PREFERENCES_FILE_NAME = "MonkiBoxPrefs"
    private const val KEY_HISTORY = "purchase_history"
    private val gson = Gson()

    private fun getPreferences(context: Context) =
        context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun getAllPurchases(context: Context): List<Purchase> {
        val json = getPreferences(context).getString(KEY_HISTORY, null)
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<Purchase>>() {}.type
        return gson.fromJson(json, type)
    }

    // Guardamos la lista completa
    private fun savePurchases(context: Context, purchaseList: List<Purchase>) {
        val editor = getPreferences(context).edit()
        val json = gson.toJson(purchaseList)
        editor.putString(KEY_HISTORY, json)
        editor.apply()
    }

    // Función para AÑADIR una nueva compra
    fun addPurchase(context: Context, purchase: Purchase) {
        val currentList = getAllPurchases(context).toMutableList()
        currentList.add(0, purchase) // Añade la más nueva al principio
        savePurchases(context, currentList)
    }
}