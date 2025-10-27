package com.example.monkibox
import com.example.monkibox.admin.AdminHomeScreen
import com.example.monkibox.admin.AdminProductsScreen
import com.example.monkibox.admin.AdminUsersScreen
import com.example.monkibox.login.LoginScreen
import com.example.monkibox.login.RegisterScreen
import com.example.monkibox.login.UserStorage
import com.example.monkibox.usuario.UserHomeScreen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // AppTheme {}
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Necesitamos el contexto para usar UserStorage
    val context = LocalContext.current

    // Definimos las rutas
    val routeLogin = "login"
    val routeRegister = "register"
    val routeUserHome = "main/{email}"
    val routeAdminHome = "admin"
    val routeAdminProducts = "admin_producto"
    val routeAdminUsers = "admin_usuarios"

    NavHost(
        navController = navController,
        startDestination = routeLogin
    ) {

        // --- Ruta de Login ---
        composable(route = routeLogin) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // --- LÓGICA DE LOGIN ---

                    // 1. Definimos el admin (puedes cambiar esto)
                    val adminEmail = "admin@monkibox.com"
                    val adminPass = "admin123"

                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Campos vacíos", Toast.LENGTH_SHORT).show()

                    } else if (email == adminEmail && password == adminPass) {
                        // 2. Es Admin
                        Toast.makeText(context, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                        navController.navigate(routeAdminHome) {
                            // Borramos la pila de navegación para que no pueda "volver" al login
                            popUpTo(routeLogin) { inclusive = true }
                        }

                    } else if (UserStorage.checkLogin(context, email, password)) {
                        // 3. Es Usuario (verificamos con SharedPreferences)
                        Toast.makeText(context, "Bienvenido Usuario", Toast.LENGTH_SHORT).show()
                        navController.navigate("main/$email") {
                            popUpTo(routeLogin) { inclusive = true }
                        }

                    } else {
                        // 4. Datos incorrectos
                        Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                },
                onRegisterClick = {
                    navController.navigate(routeRegister)
                }
            )
        }

        // --- Ruta de Registro ---
        composable(route = routeRegister) {
            RegisterScreen(
                onRegisterClick = { email, password ->
                    // --- LÓGICA DE REGISTRO ---
                    // Guardamos el usuario usando nuestra clase UserStorage
                    UserStorage.saveUser(context, email, password)

                    // Mostramos notificación
                    Toast.makeText(context, "¡Usuario registrado con éxito!", Toast.LENGTH_SHORT)
                        .show()

                    // Lo mandamos de vuelta al Login
                    navController.popBackStack()
                },
                onBackToLoginClick = {
                    // Vuelve a la pantalla anterior (login)
                    navController.popBackStack()
                }
            )
        }

        // --- Ruta "main.kt" (Home del Usuario) ---
        composable(
            route = routeUserHome,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            // Obtenemos el email de la ruta
            val email = backStackEntry.arguments?.getString("email") ?: "Usuario"

            // Reemplazamos el placeholder por la pantalla real
            UserHomeScreen(
                email = email,
                onLogoutClick = {
                    // Esta es la acción para volver al Login
                    navController.navigate(routeLogin) {
                        // Borramos toda la pila de navegación del usuario
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // --- Ruta Home del Admin ---
        composable(route = routeAdminHome) {
            AdminHomeScreen(
                onManageProductsClick = {
                    navController.navigate(routeAdminProducts)
                },
                onManageUsersClick = {
                    navController.navigate(routeAdminUsers)
                },
                // ¡AÑADE ESTO!
                onLogoutClick = {
                    // Navega de vuelta al login
                    navController.navigate(routeLogin) {
                        // Borra TODO el historial de navegación ("back stack")
                        // para que el admin no pueda "volver" con el botón
                        // de retroceso del celular.
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Ruta "admin_producto.kt"
        composable(route = routeAdminProducts) {
            AdminProductsScreen(
                onBackClick = {
                    navController.popBackStack() // Vuelve a la pantalla anterior
                }
            )
        }

        // Ruta "admin_usuarios.kt"
        composable(route = routeAdminUsers) {
            AdminUsersScreen(
                onBackClick = {
                    navController.popBackStack() // Vuelve a la pantalla anterior
                }
            )
        }
    }
}

@Composable
fun AdminHomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Bienvenido a la Home del ADMIN")
    }
}