package com.example.monkibox.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Usamos @OptIn para componentes que aún son "Experimentales" en Material 3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onManageProductsClick: () -> Unit,
    onManageUsersClick: () -> Unit
) {
    // 1. GESTIÓN DE ESTADO: Estado para el menú lateral (abierto/cerrado)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 'scope' se usa para abrir y cerrar el menú con una animación (corutina)
    val scope = rememberCoroutineScope()

    // 2. EL CONTENEDOR PRINCIPAL: ModalNavigationDrawer
    ModalNavigationDrawer(
        drawerState = drawerState, // Controla si está abierto o cerrado

        // 3. EL CONTENIDO DEL MENÚ (lo que se desliza)
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Botón 'X' para cerrar
                    IconButton(
                        onClick = {
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.align(Alignment.End) // Lo alinea a la derecha
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar menú"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón "Gestionar productos"
                    NavigationDrawerItem(
                        label = { Text("Gestionar productos") },
                        selected = false, // 'false' para que no se quede marcado
                        onClick = {
                            scope.launch { drawerState.close() } // Cierra el menú
                            onManageProductsClick() // Llama a la navegación
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón "Gestionar Usuarios"
                    NavigationDrawerItem(
                        label = { Text("Gestionar Usuarios") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() } // Cierra el menú
                            onManageUsersClick() // Llama a la navegación
                        }
                    )
                }
            }
        }
    ) {
        // 4. EL CONTENIDO PRINCIPAL DE LA PANTALLA (AdminHomeScreen)
        // Scaffold nos da la estructura de TopBar + Contenido
        Scaffold(
            topBar = {
                // La barra superior
                TopAppBar(
                    title = {
                        // El título que pediste
                        Text("Bienvenido Administrador")
                    },
                    navigationIcon = {
                        // El botón de "hamburguesa" (tres rayitas)
                        IconButton(onClick = {
                            scope.launch { drawerState.open() } // Abre el menú
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Contenido de la página (debajo de la TopBar)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Selecciona una opción del menú lateral")
            }
        }
    }
}