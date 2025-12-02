package com.example.monkibox.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkibox.dataclass.User
import com.example.monkibox.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // 1. Estado para la lista de usuarios (Para el Admin)
    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    // 2. Estado para saber si el login/registro fue exitoso
    // Usaremos un Flow para comunicar el resultado a la UI (MainActivity)
    private val _authStatus = MutableStateFlow<AuthResult?>(null)
    val authStatus: StateFlow<AuthResult?> = _authStatus.asStateFlow()

    // Clase sellada para manejar los resultados de Auth
    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    // --- FUNCIONES ---

    fun clearAuthStatus() {
        _authStatus.value = null
    }

    // A. LOGIN (Conectado a Spring Boot)
    fun login(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loginUser = User(email = email, password = pass)
                val response = RetrofitClient.userApi.login(loginUser)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _authStatus.value = AuthResult.Success(user)

                    // Guardamos el usuario en local para mantener la sesión
                    // (Podrías usar UserStorage para guardar solo el email/rol como "caché")
                } else {
                    _authStatus.value = AuthResult.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _authStatus.value = AuthResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // B. REGISTRO (Conectado a Spring Boot)
    fun register(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newUser = User(email = email, password = pass)
                val response = RetrofitClient.userApi.register(newUser)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _authStatus.value = AuthResult.Success(user)
                } else {
                    // Si falla (ej: correo duplicado), el backend manda un 400
                    _authStatus.value = AuthResult.Error("Error al registrar. ¿Correo en uso?")
                }
            } catch (e: Exception) {
                _authStatus.value = AuthResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // C. LISTAR USUARIOS (Para Admin)
    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = RetrofitClient.userApi.getAllUsers()
                _userList.value = users
            } catch (e: Exception) {
                Log.e("API", "Error cargando usuarios: ${e.message}")
            }
        }
    }

    // C. LOGIN DE INVITADO (Sin backend)
    fun loginAsGuest() {
        // Creamos un usuario temporal en memoria
        val guestUser = User(
            id = -1, // ID negativo para identificarlo
            email = "invitado@monkibox.com",
            password = "",
            role = "GUEST" // Rol nuevo
        )
        _authStatus.value = AuthResult.Success(guestUser)
    }
}