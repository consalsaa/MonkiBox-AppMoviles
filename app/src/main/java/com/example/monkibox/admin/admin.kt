package com.example.monkibox.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.monkibox.R
import com.example.monkibox.viewmodels.HistoryViewModel
import com.example.monkibox.viewmodels.ProductViewModel
import com.example.monkibox.viewmodels.UserViewModel

// --- DEFINICIÓN DE COLORES DEL TEMA MONKIBOX ---
val MonkiAmarillo = Color(0xFFFFC107)
val MonkiCafe = Color(0xFF6D4C41)
val MonkiFondo = Color(0xFFFAF0E6)
val MonkiAmarilloSuave = Color(0xFFFFEBEE)

// --- ESTRUCTURA DE DATOS PARA EL DASHBOARD ---
data class AdminStat(
    val title: String,
    val value: String,
    val icon: @Composable () -> Unit // Función para colocar cualquier icono o imagen.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onManageProductsClick: () -> Unit,
    onManageUsersClick: () -> Unit,
    onLogoutClick: () -> Unit,
    productViewModel: ProductViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    // Observamos los datos para las estadísticas
    val productList by productViewModel.productList.collectAsState()
    val userList by userViewModel.userList.collectAsState()
    val purchaseList by historyViewModel.purchaseList.collectAsState()

    // Y Cargamos los datos al entrar
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
        userViewModel.loadUsers()
        historyViewModel.loadPurchaseHistory()
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Colores MonkiBox
    val monkiBackground = Color(0xFFEFEADC)
    val monkiBrown = Color(0xFFA75A17)
    val monkiYellow = Color(0xFFFBCC05)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MonkiFondo
            ) {
                // Header del Menú Lateral (Área de Marca)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MonkiCafe)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Icono MonkiBox en el menú.
                    Image(
                        painter = painterResource(R.drawable.mono),
                        contentDescription = "MonkiBox Logo",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Panel de Control",
                        color = Color.White, // Texto en blanco para alto contraste
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "¡Hola, Administrador!",
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opciones de Navegación

                NavigationDrawerItem(
                    // Color del texto
                    label = { Text("Gestionar Productos", color = MonkiCafe) },
                    icon = { Icon(
                        painter = painterResource(R.drawable.inventario),
                        contentDescription = null,
                        tint = MonkiCafe,
                        modifier = Modifier.size(32.dp)
                    )},
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onManageProductsClick()
                    },
                    // Color de resaltado sutil al seleccionar un ítem.
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MonkiAmarilloSuave
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Gestionar Usuarios", color = MonkiCafe) },
                    icon = { Icon(
                        painter = painterResource(R.drawable.usuario),
                        contentDescription = null,
                        tint = MonkiCafe,
                        modifier = Modifier.size(32.dp)
                    ) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onManageUsersClick()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MonkiAmarilloSuave
                    )
                )

                // Divisor para separar opciones.
                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray)

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    selected = false,
                    onClick = {
                        // Lógica de cerrar sesión
                        scope.launch { drawerState.close() } // Cierra el menú
                        onLogoutClick()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administración", color = monkiBrown, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = monkiBackground),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menú", tint = monkiBrown)
                        }
                    }
                )
            },
            containerColor = monkiBackground // Color de fondo crema
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Scroll por si acaso
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- SECCIÓN 1: ESTADÍSTICAS (Imagen 1) ---
                Text(
                    text = "Resumen de MonkiBox",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tarjeta Productos
                    StatCard(
                        title = "TOTAL PRODUCTOS",
                        count = productList.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    // Tarjeta Usuarios
                    StatCard(
                        title = "TOTAL USUARIOS",
                        count = userList.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    // Tarjeta Ventas (Compras)
                    StatCard(
                        title = "TOTAL COMPRAS",
                        count = purchaseList.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN 2: ACCIONES RÁPIDAS (Imagen 2) ---
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón Gestionar Productos (Icono Inventario)
                    QuickActionCard(
                        title = "Inventario\nTotal", // Salto de línea para estilo
                        icon = Icons.Default.Inventory,
                        color = monkiYellow,
                        iconColor = monkiBrown,
                        onClick = onManageProductsClick,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón Gestionar Usuarios (Icono Persona)
                    QuickActionCard(
                        title = "Gestionar\nUsuarios",
                        icon = Icons.Default.Group,
                        color = monkiYellow, // Puedes cambiar a blanco si prefieres
                        iconColor = monkiBrown,
                        onClick = onManageUsersClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// --- COMPOSABLE: TARJETA DE ESTADÍSTICA (Blanca y limpia) ---
@Composable
fun StatCard(title: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA75A17) // Color Café Monki
            )
        }
    }
}

// --- COMPOSABLE: BOTÓN DE ACCIÓN RÁPIDA (Estilo Imagen 2) ---
@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color, // Color del borde o icono
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono grande a la izquierda (o arriba según diseño, aquí lo pongo al lado como tarjeta)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Texto
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}