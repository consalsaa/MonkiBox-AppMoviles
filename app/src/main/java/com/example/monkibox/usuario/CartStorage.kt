package com.example.monkibox.usuario

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartStorage {
    private const val PREFERENCES_FILE_NAME = "MonkiBoxPrefs"
    private const val KEY_CART = "cart_list"
    private val gson = Gson()

    private fun getPreferences(context: Context) =
        context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun getAllCartItems(context: Context): List<CartItem> {
        val json = getPreferences(context).getString(KEY_CART, null)
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCartItems(context: Context, cartList: List<CartItem>) {
        val editor = getPreferences(context).edit()
        val json = gson.toJson(cartList)
        editor.putString(KEY_CART, json)
        editor.apply()
    }
}