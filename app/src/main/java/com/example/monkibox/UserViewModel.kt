package com.example.monkibox
import com.example.monkibox.login.UserStorage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // 1. Estado para la lista de usuarios
    private val _userList = MutableStateFlow<List<String>>(emptyList())
    val userList: StateFlow<List<String>> = _userList.asStateFlow()

    // 2. Cargamos los usuarios al iniciar el ViewModel
    init {
        loadUsers()
    }

    // 3. Función de carga en un hilo de fondo
    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _userList.value = UserStorage.getAllUsers(context).toList()
        }
    }
}