package com.example.monkibox.usuario
import com.example.monkibox.viewmodels.CartViewModel
import com.example.monkibox.viewmodels.ProductViewModel
import com.example.monkibox.viewmodels.HistoryViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel


// Definimos las rutas de las pestañas internas
sealed class UserScreen(val route: String, val icon: ImageVector, val label: String) {
    object Home : UserScreen("home", Icons.Default.Home, "Home")
    object Products : UserScreen("products", Icons.Default.Archive, "Productos")
    object History : UserScreen("history", Icons.Default.ReceiptLong, "Historial")
    object Cart : UserScreen("cart", Icons.Default.ShoppingCart, "Carrito")
    object More : UserScreen("more", Icons.Default.Menu, "Más")
}

// Lista de las pestañas para la barra
val userTabs = listOf(
    UserScreen.Home,
    UserScreen.Products,
    UserScreen.History,
    UserScreen.Cart,
    UserScreen.More
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(email: String, onLogoutClick: () -> Unit) {

    // Creamos un NUEVO NavController para las pestañas
    val tabNavController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()

    val monkiBackground = Color(0xFFEFEADC)
    val monkiBrown = Color(0xFFA75A17)

    // El Scaffold nos da la estructura de la barra inferior
    Scaffold(
        containerColor = monkiBackground,
        bottomBar = {
            // 4. La Barra de Navegación
            NavigationBar (containerColor = Color(0xFFE7D8B8)){
                // Obtenemos la ruta actual para saber qué icono marcar
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Creamos un ítem por cada pestaña en nuestra lista 'userTabs'
                userTabs.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        // Marcamos el ítem si su ruta es la actual
                        selected = currentRoute == screen.route,
                        onClick = {
                            when (screen.route) {
                                UserScreen.Cart.route -> cartViewModel.loadCart()
                                UserScreen.History.route -> historyViewModel.loadPurchaseHistory()
                            }

                            tabNavController.navigate(screen.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        val productDetailRoute = "product_detail"
        val aboutRoute = "about_monkibox"
        // El NAVEGADOR ANIDADO
        // Este NavHost muestra la pantalla de la pestaña seleccionada
        NavHost(
            navController = tabNavController,
            startDestination = UserScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Ruta para "Home"
            composable(UserScreen.Home.route) {
                // Extraemos el nombre del email
                val userName = email.substringBefore('@').replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                // 7. Pantalla de Bienvenida (Tu "main.kt" real)
                UserHomeContent(name = userName)
            }

            // Ruta para "Productos"
            composable(UserScreen.Products.route) {
                ProductsScreen(
                    viewModel = productViewModel, // Pasa el VM de producto
                    onProductClick = { product ->
                        tabNavController.navigate("$productDetailRoute/${product.id}")
                    }
                )
            }

            // Ruta para "Historial"
            composable(UserScreen.History.route) {
                HistoryScreen(viewModel = historyViewModel)
            }

            // Ruta para "Carrito"
            composable(UserScreen.Cart.route) {
                CartScreen(viewModel = cartViewModel)
            }

            // Ruta para detalle
            composable(
                route = "$productDetailRoute/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")

                if (productId != null) {
                    // PASAMOS AMBAS INSTANCIAS AL DETALLE
                    ProductDetailScreen(
                        productId = productId,
                        viewModel = productViewModel,  // Pasa el VM de producto
                        cartViewModel = cartViewModel, // Pasa el VM de carrito
                        onBackClick = {
                            tabNavController.popBackStack()
                        }
                    )
                } else {
                    Text("Error: ID de producto no encontrado")
                }
            }

            // "MÁS"
            composable(UserScreen.More.route) {
                MoreScreen(
                    userEmail = email,
                    onHomeClick = {
                        // Volver a Inicio
                        tabNavController.navigate(UserScreen.Home.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id)
                        }
                    },
                    onHistoryClick = {
                        // Ir a Historial
                        historyViewModel.loadPurchaseHistory()
                        tabNavController.navigate(UserScreen.History.route)
                    },
                    onAboutClick = {
                        // Ir a Acerca De
                        tabNavController.navigate(aboutRoute)
                    },
                    onLogoutClick = onLogoutClick // Pasamos la función de cerrar sesión
                )
            }

            // "ACERCA DE"
            composable(aboutRoute) {
                AboutScreen(
                    onBackClick = { tabNavController.popBackStack() }
                )
            }
        }
    }
}

// 7. Contenido de la pestaña "Home" (main.kt)
@Composable
fun UserHomeContent(name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // El texto de bienvenida que pediste
        Text(
            text = "¡Bienvenid@, $name!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}