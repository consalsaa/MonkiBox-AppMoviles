package com.example.monkibox.usuario
import com.example.monkibox.CartViewModel
import com.example.monkibox.CartTotals
import com.example.monkibox.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.example.monkibox.ui.theme.MonkiAmarillo
import com.example.monkibox.ui.theme.MonkiAmarilloSuave
import com.example.monkibox.ui.theme.MonkiCafe
import com.example.monkibox.ui.theme.MonkiFondo

@Composable
fun CartScreen(viewModel: CartViewModel) {
    // 1. Observamos los estados del ViewModel
    val cartItems by viewModel.cartItems.collectAsState()
    val totals by viewModel.cartTotals.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonkiFondo)
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Carrito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MonkiCafe,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. Lista Scrolleable de Productos
        LazyColumn(
            modifier = Modifier.weight(1f), // Ocupa el espacio disponible
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems, key = { it.id }) { item ->
                CartItemRow(
                    item = item,
                    onRemove = { viewModel.removeItem(item.id) },
                    onQuantityChange = { newQty ->
                        viewModel.updateQuantity(item.id, newQty)
                    }
                )
            }
        }

        // 3. Resumen de Totales
        CartTotalsSummary(totals = totals)

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Botón de Realizar Compra
        Button(
            onClick = {
                viewModel.checkout {
                    // Esta es la acción onSuccess
                    Toast.makeText(context, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show()
                    // (La UI se actualizará sola porque el ViewModel vacía el carrito)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonkiAmarillo,
                contentColor = MonkiCafe
            ),
            // No se puede comprar si el carrito está vacío
            enabled = cartItems.isNotEmpty()
        ) {
            Text("Realizar compra", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


/**
 * Composable para cada fila del carrito (basado en tu imagen)
 */
@Composable
fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MonkiAmarilloSuave),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.mono),
                error = painterResource(id = R.drawable.mono)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de Información (Nombre, Eliminar, Cantidad)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MonkiCafe
                )

                // Botón "Eliminar" (como en tu imagen)
                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.padding(0.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = MonkiCafe)
                ) {
                    Text("Eliminar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Selector de cantidad (simplificado)
                // TODO: Reemplazar por un Dropdown como en tu imagen si es necesario
                QuantitySelectorSimple(
                    quantity = item.quantity,
                    onQuantityChange = onQuantityChange
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Precio Total del Artículo
            Text(
                text = "$${String.format("%.2f", item.product.price * item.quantity)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MonkiAmarillo
            )
        }
    }
}


/**
 * Resumen de totales (Subtotal, Envío, Impuestos, Total)
 */
@Composable
fun CartTotalsSummary(totals: CartTotals) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TotalRow(label = "Subtotal", amount = totals.subtotal)
        TotalRow(label = "Envío", amount = totals.shipping)
        TotalRow(label = "Impuestos (19%)", amount = totals.taxes)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Fila del TOTAL en negrita
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MonkiCafe
            )
            Text(
                text = "$${String.format("%.2f", totals.total)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MonkiCafe
            )
        }
    }
}

// Helper para las filas de totales
@Composable
fun TotalRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = MonkiCafe.copy(alpha = 0.8f))
        Text(text = "$${String.format("%.2f", amount)}", fontSize = 16.sp, color = MonkiCafe)
    }
}

// Selector de cantidad simple (reutilizado de ProductDetail)
@Composable
fun QuantitySelectorSimple(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconButton(
            onClick = { onQuantityChange(quantity - 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Text("-")
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // Botón Quitar (-)
        OutlinedIconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MonkiCafe),
            border = BorderStroke(1.dp, MonkiCafe)
        ) {
            Text("+")
        }
    }
}