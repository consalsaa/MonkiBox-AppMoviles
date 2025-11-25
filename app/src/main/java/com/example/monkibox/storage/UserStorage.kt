package com.example.monkibox.storage

import android.content.Context
import android.content.SharedPreferences

// Esta clase nos ayudará a guardar y leer los datos del usuario
object UserStorage {

    private const val PREFERENCES_FILE_NAME = "MonkiBoxPrefs"

    // Guardaremos una lista de correos
    private const val KEY_USER_LIST = "user_list"
    // Usaremos un prefijo para guardar cada contraseña
    private const val KEY_PASSWORD_PREFIX = "pass_for_"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    // FUNCIÓN PARA GUARDAR UN USUARIO
    fun saveUser(context: Context, email: String, password: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()

        // Obtenemos la lista actual de usuarios
        // (Usamos toMutableSet() para poder añadir el nuevo)
        val userList = prefs.getStringSet(KEY_USER_LIST, emptySet())?.toMutableSet() ?: mutableSetOf()

        // Añadimos el nuevo email a la lista
        userList.add(email)

        // Guardamos la lista actualizada
        editor.putStringSet(KEY_USER_LIST, userList)

        // Guardamos la contraseña usando el email como clave
        editor.putString(KEY_PASSWORD_PREFIX + email, password)

        editor.apply()
    }

    // FUNCIÓN PARA VERIFICAR EL LOGIN
    fun checkLogin(context: Context, email: String, password: String): Boolean {
        val prefs = getPreferences(context)

        // Buscamos la contraseña específica para ESE email
        val savedPassword = prefs.getString(KEY_PASSWORD_PREFIX + email, null)

        return savedPassword == password
    }

    // Obtener todos los usuarios
    fun getAllUsers(context: Context): Set<String> {
        val prefs = getPreferences(context)
        return prefs.getStringSet(KEY_USER_LIST, emptySet()) ?: emptySet()
    }

    // Contar usuarios
    fun getUserCount(context: Context): Int {
        return getAllUsers(context).size
    }

    // Función clearUser
    fun clearUser(context: Context) {
        // Esta función ahora solo borraría al usuario "logueado",
        // para borrar TODO, necesitaríamos un .clear()
        // Por ahora, la dejamos simple.
    }
}