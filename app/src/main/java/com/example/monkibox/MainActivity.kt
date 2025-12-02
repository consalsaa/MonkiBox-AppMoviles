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

    // Ahora aceptamos rol en el usuario también (para diferenciar Cliente de Invitado)
    val routeUserHome = "main/{email}/{role}"

    // Ahora aceptamos rol en el admin (para diferenciar Admin de Soporte)
    val routeAdminHome = "admin/{role}"

    val routeAdminProducts = "admin_producto"
    val routeAdminUsers = "admin_usuarios"

    // "Efecto Secundario" para manejar la respuesta del servidor
    // Esto se ejecuta automáticamente cuando 'authState' cambia (Success o Error)
    LaunchedEffect(authState) {
        when (val result = authState) {
            is UserViewModel.AuthResult.Success -> {
                val user = result.user
                Toast.makeText(context, "Bienvenido ${user.email} (${user.role})", Toast.LENGTH_SHORT).show()

                if (user.role == "ADMIN" || user.role == "SUPPORT") {
                    // Vamos al panel de Admin pasando el rol
                    navController.navigate("admin/${user.role}") {
                        popUpTo(routeLogin) { inclusive = true }
                    }
                } else {
                    // USER o GUEST van al home de usuario
                    // Si es guest, el email puede ser "invitado"
                    navController.navigate("main/${user.email}/${user.role}") {
                        popUpTo(routeLogin) { inclusive = true }
                    }
                }
                userViewModel.clearAuthStatus()
            }
            is UserViewModel.AuthResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                userViewModel.clearAuthStatus()
            }
            null -> { }
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
                    userViewModel.login(email, password)
                },
                onRegisterClick = {
                    navController.navigate(routeRegister)
                },
                onGuestClick = { // <--- CONEXIÓN NUEVA
                    userViewModel.loginAsGuest()
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

        // --- Ruta Home del Usuario ---
        composable(
            route = routeUserHome,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: "Usuario"
            val role = backStackEntry.arguments?.getString("role") ?: "USER"

            UserHomeScreen(
                email = email,
                userRole = role, // Necesitas actualizar UserHomeScreen para recibir esto
                onLogoutClick = {
                    navController.navigate(routeLogin) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // --- Ruta Home del Admin ---
        composable(
            route = routeAdminHome,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "ADMIN"

            // Calculamos si tiene permiso total
            val isAdmin = (role == "ADMIN")

            AdminHomeScreen(
                isAdmin = isAdmin, // Pasamos el booleano
                onManageProductsClick = { navController.navigate(routeAdminProducts) },
                onManageUsersClick = { navController.navigate(routeAdminUsers) },
                onLogoutClick = {
                    navController.navigate(routeLogin) {
                        popUpTo(navController.graph.id) { inclusive = true }
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