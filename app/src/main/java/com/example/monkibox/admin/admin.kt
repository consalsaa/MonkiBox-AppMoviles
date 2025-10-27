package com.example.monkibox.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import kotlinx.coroutines.launch
import com.example.monkibox.R

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
    onLogoutClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Creación de la lista de datos para llenar el Dashboard.
    val stats = listOf(
        AdminStat("Inventario Total", "540", { Icon(painter = painterResource(R.drawable.inventario), contentDescription = null, tint = MonkiAmarillo) }),
        AdminStat("Usuarios totales", "540", { Icon(painter = painterResource(R.drawable.usuario), contentDescription = null, tint = MonkiAmarillo) }),
    )

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
        // 4. CONTENIDO PRINCIPAL (Scaffold)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Bienvenido Administrador",
                            color = MonkiCafe,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    //  Colores de la barra superior.
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MonkiFondo,
                        navigationIconContentColor = MonkiCafe
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            containerColor = MonkiFondo
        ) { paddingValues ->

            // Contenido del Dashboard (en lugar del texto simple)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                //  Título del Dashboard.
                Text(
                    text = "Resumen de MonkiBox",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonkiCafe,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )

                // LazyVerticalGrid para mostrar estadísticas en un diseño de cuadrícula.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Define 2 columnas por fila.
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Crea un StatCard por cada elemento en la lista 'stats'.
                    items(stats.size) { index ->
                        StatCard(stats[index])
                    }
                }
            }
        }
    }
}

// --- NUEVA IMPLEMENTACIÓN: FUNCIÓN COMPOSABLE PARA UNA TARJETA DE ESTADÍSTICA ---
@Composable
fun StatCard(stat: AdminStat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        // NUEVA IMPLEMENTACIÓN: Fondo de la tarjeta en Blanco puro para contraste.
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        // NUEVA IMPLEMENTACIÓN: Sombra sutil para un diseño más elegante y tridimensional.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                stat.icon()
                Spacer(modifier = Modifier.width(8.dp))
                // Título de la estadística en Marrón.
                Text(
                    text = stat.title,
                    color = MonkiCafe,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // NUEVA IMPLEMENTACIÓN: El valor numérico se muestra grande y en MonkiYellow (acento).
            Text(
                text = stat.value,
                color = MonkiAmarillo,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewAdminHomeScreen() {
    AdminHomeScreen({}, {}, {})
}