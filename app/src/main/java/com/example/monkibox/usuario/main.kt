package com.example.monkibox.usuario
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import com.example.monkibox.CartViewModel
import com.example.monkibox.ProductViewModel
import com.example.monkibox.HistoryViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monkibox.R
import com.example.monkibox.ui.theme.MonkiAmarillo
import com.example.monkibox.ui.theme.MonkiAmarilloSuave
import com.example.monkibox.ui.theme.MonkiCafe
import com.example.monkibox.ui.theme.MonkiFondo


// 1. Definimos las rutas de las pestañas internas
sealed class UserScreen(val route: String, val icon: ImageVector, val label: String) {
    object Home : UserScreen("home", Icons.Default.Home, "Home")
    object Products : UserScreen("products", Icons.Default.Archive, "Productos")
    object History : UserScreen("history", Icons.Default.ReceiptLong, "Historial")
    object Cart : UserScreen("cart", Icons.Default.ShoppingCart, "Carrito")
    object Profile : UserScreen("profile", Icons.Default.Person, "Perfil")
}

// Lista de las pestañas para la barra
val userTabs = listOf(
    UserScreen.Home,
    UserScreen.Products,
    UserScreen.History,
    UserScreen.Cart,
    UserScreen.Profile
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(email: String, onLogoutClick: () -> Unit) {

    // 2. Creamos un NUEVO NavController para las pestañas
    val tabNavController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()

    // 3. El Scaffold nos da la estructura de la barra inferior
    Scaffold(
        containerColor = MonkiFondo,
        bottomBar = {
            NavigationBar(
                containerColor = MonkiFondo
            ) {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                userTabs.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MonkiCafe,
                            unselectedIconColor = MonkiCafe.copy(alpha = 0.6f),
                            selectedTextColor = MonkiCafe,
                            unselectedTextColor = MonkiCafe.copy(alpha = 0.6f),
                            indicatorColor = MonkiAmarilloSuave
                        ),
                        onClick = {
                            if (screen.route == UserScreen.Profile.route) {
                                onLogoutClick()
                            } else {
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
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        val productDetailRoute = "product_detail"
        // 6. El NAVEGADOR ANIDADO
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
                UserHomeContent(
                    name = userName,
                    onNavigateToProducts = { tabNavController.navigate(UserScreen.Products.route) },
                    onNavigateToCart = { tabNavController.navigate(UserScreen.Cart.route) }
                )
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

            // --- 3. AÑADIMOS LA NUEVA RUTA DE DETALLE ---
            composable(
                route = "$productDetailRoute/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")

                if (productId != null) {
                    // 2. PASAMOS AMBAS INSTANCIAS AL DETALLE
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
        }
    }
}

// 7. Contenido de la pestaña "Home" (main.kt)
@Composable
fun UserHomeContent(
    name: String,
    onNavigateToProducts: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // El fondo ya está en el Scaffold, pero se añade un padding interno
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 1. MASCOTA Y BIENVENIDA
        Image(
            painter = painterResource(id = R.drawable.mono),
            contentDescription = "MonkiBox Mascota",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto de Bienvenida
        Text(
            text = "¡Bienvenid@, $name!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MonkiCafe // Color Marrón
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 2. TÍTULO DE ACCIÓN
        Text(
            text = "Explora nuestros productos:",
            style = MaterialTheme.typography.titleMedium,
            color = MonkiCafe
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón 1: Ver Colección (Amarillo de acento)
        Button(
            onClick = onNavigateToProducts,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonkiAmarillo, // Amarillo Acento
                contentColor = MonkiCafe // Texto Marrón
            )
        ) {
            Text("Ver Productos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón 2: Ver mi Carrito (Outline/Secundario)
        OutlinedButton(
            onClick = onNavigateToCart,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            border = BorderStroke(2.dp, MonkiCafe),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MonkiCafe
            )
        ) {
            Text("Ver mi Carrito", fontSize = 18.sp)
        }
    }
}