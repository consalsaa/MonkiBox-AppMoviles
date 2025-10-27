package com.example.monkibox.usuario
import com.example.monkibox.HistoryViewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel() // 1. Obtenemos el ViewModel
) {
    // 2. Observamos la lista de compras
    val purchaseList by viewModel.purchaseList.collectAsState()

    // 3. Estado para controlar qué diálogo mostrar
    var selectedPurchase by remember { mutableStateOf<Purchase?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Historial de boletas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 4. Lista Scrolleable de Boletas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(purchaseList, key = { it.id }) { purchase ->
                PurchaseItemCard(
                    purchase = purchase,
                    onClick = {
                        // Al hacer clic, guardamos la boleta seleccionada
                        selectedPurchase = purchase
                    }
                )
            }
        }
    }

    // 5. El Diálogo (se muestra si 'selectedPurchase' no es null)
    selectedPurchase?.let { purchase ->
        PurchaseDetailDialog(
            purchase = purchase,
            onDismiss = {
                // Al cerrar, limpiamos la selección
                selectedPurchase = null
            }
        )
    }
}

/**
 * El "cuadro" de información rápida para cada boleta en la lista.
 */
@Composable
fun PurchaseItemCard(
    purchase: Purchase,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Hacemos toda la tarjeta clickeable
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna Izquierda: ID y Cantidad
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ID: ${purchase.id.substring(0, 8)}", // ID corto
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Cantidad: ${purchase.items.sumOf { it.quantity }} productos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Columna Derecha: Flecha y Total
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Ver detalle",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$${String.format("%.2f", purchase.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * El Diálogo emergente que muestra el detalle de la boleta.
 */
@Composable
fun PurchaseDetailDialog(
    purchase: Purchase,
    onDismiss: () -> Unit
) {
    // Helper para formatear la fecha
    val formattedDate = remember {
        val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy 'a las' HH:mm", Locale.getDefault())
        sdf.format(Date(purchase.date))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f) // 95% del ancho
                .fillMaxHeight(0.85f) // 85% del alto
        ) {
            Column {
                // 1. Título y Botón de Cerrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle de la Boleta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }

                // Columna scrolleable para el contenido
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("ID: ${purchase.id}", fontSize = 12.sp, color = Color.Gray)
                    Text("Fecha: $formattedDate", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Todos los Totales
                    TotalRow(label = "Subtotal", amount = purchase.subtotal)
                    TotalRow(label = "Envío", amount = purchase.shipping)
                    TotalRow(label = "Impuestos", amount = purchase.taxes)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    TotalRow(
                        label = "Total Pagado",
                        amount = purchase.total,
                        isBold = true // Fila de total en negrita
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Título "Productos"
                    Text(
                        text = "Productos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Lista de Productos
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        purchase.items.forEach { item ->
                            DialogProductRow(item = item)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila para cada producto DENTRO del diálogo
 */
@Composable
fun DialogProductRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "(${item.quantity}x)",
            modifier = Modifier.width(40.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.product.name,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$${String.format("%.2f", item.product.price * item.quantity)}",
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Helper para las filas de totales (copiado de CartScreen)
 */
@Composable
fun TotalRow(label: String, amount: Double, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 18.sp else 16.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
        Text(
            text = "$${String.format("%.2f", amount)}",
            fontSize = if (isBold) 18.sp else 16.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        )
    }
}