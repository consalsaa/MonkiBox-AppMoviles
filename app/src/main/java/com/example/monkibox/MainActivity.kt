package com.example.monkibox

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Importaciones de pantallas
import com.example.monkibox.admin.AdminHomeScreen
import com.example.monkibox.admin.AdminProductsScreen
import com.example.monkibox.admin.AdminUsersScreen
import com.example.monkibox.login.LoginScreen
import com.example.monkibox.login.RegisterScreen
import com.example.monkibox.usuario.UserHomeScreen

// Importamos el ViewModel
import com.example.monkibox.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // AppTheme { ... }
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Instanciamos el UserViewModel (Gestor de Autenticación con Backend)
    val userViewModel: UserViewModel = viewModel()

    // Observamos el estado de autenticación
    val authState by userViewModel.authStatus.collectAsState()

    // Definimos las rutas
    val routeLogin = "login"
    val routeRegister = "register"
    val routeUserHome = "main/{email}"
    val routeAdminHome = "admin"
    val routeAdminProducts = "admin_producto"
    val routeAdminUsers = "admin_usuarios"

    // "Efecto Secundario" para manejar la respuesta del servidor
    // Esto se ejecuta automáticamente cuando 'authState' cambia (Success o Error)
    LaunchedEffect(authState) {
        when (val result = authState) {
            is UserViewModel.AuthResult.Success -> {
                val user = result.user
                Toast.makeText(context, "Bienvenido ${user.email}", Toast.LENGTH_SHORT).show()

                // Verificamos el ROL que viene de la Base de Datos (Spring Boot)
                if (user.role == "ADMIN") {
                    navController.navigate(routeAdminHome) {
                        popUpTo(routeLogin) { inclusive = true }
                    }
                } else {
                    // Si es USER, vamos al home de usuario
                    navController.navigate("main/${user.email}") {
                        popUpTo(routeLogin) { inclusive = true }
                    }
                }
                // Limpiamos el estado para evitar re-navegación
                userViewModel.clearAuthStatus()
            }
            is UserViewModel.AuthResult.Error -> {
                // Si falló (contraseña mal, error de red, etc.)
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                userViewModel.clearAuthStatus()
            }
            null -> { /* No hacemos nada mientras esperamos */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = routeLogin
    ) {

        // --- Ruta de Login ---
        composable(route = routeLogin) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Llamamos al ViewModel.
                    // Él hablará con Spring Boot.
                    userViewModel.login(email, password)
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
                    // Llamamos al ViewModel para registrar en la BD real
                    userViewModel.register(email, password)

                    // Si el registro es exitoso, el LaunchedEffect de arriba lo detectará y navegará solo.
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Ruta "main.kt" (Home del Usuario) ---
        composable(
            route = routeUserHome,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "Usuario"

            UserHomeScreen(
                email = email,
                onLogoutClick = {
                    navController.navigate(routeLogin) {
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
                onLogoutClick = {
                    navController.navigate(routeLogin) {
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
                    navController.popBackStack()
                }
            )
        }

        // Ruta "admin_usuarios.kt"
        composable(route = routeAdminUsers) {
            AdminUsersScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}