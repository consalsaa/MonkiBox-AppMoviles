package com.example.monkibox.usuario
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.monkibox.R
import com.example.monkibox.ProductViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.monkibox.CartViewModel
import com.example.monkibox.ui.theme.MonkiAmarillo
import com.example.monkibox.ui.theme.MonkiCafe
import com.example.monkibox.ui.theme.MonkiFondo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductViewModel, // (El VM de producto)
    cartViewModel: CartViewModel, // (El VM de carrito)
    onBackClick: () -> Unit
) {
    // 1. Buscamos el producto en la lista del ViewModel usando el ID
    // Usamos 'derivedStateOf' para que solo se recalcule si el ID o la lista cambian
    val product by remember(productId, viewModel.productList.collectAsState().value) {
        derivedStateOf {
            viewModel.productList.value.find { it.id == productId }
        }
    }

    // 2. Estado para el selector de cantidad
    var quantity by remember { mutableStateOf(1) }

    val cartViewModel: CartViewModel = viewModel()

    val context = LocalContext.current

    Scaffold(
        containerColor = MonkiFondo,
        topBar = {
            // 1. La flecha sigue estando en la barra superior
            TopAppBar(
                title = { Text(product?.name ?: "Detalle", color = MonkiCafe, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MonkiCafe
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MonkiFondo,
                    navigationIconContentColor = MonkiCafe,
                    titleContentColor = MonkiCafe
                )
            )
        }
    ) { paddingValues ->

        if (product == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado")
            }
        } else {
            // 2. Columna principal que permite SCROLL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // Permite scroll si el contenido es largo
            ) {
                // 3. Imagen en grande
                AsyncImage(
                    model = product!!.imageUrl,
                    contentDescription = product!!.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.mono),
                    error = painterResource(id = R.drawable.mono)
                )

                // 4. Contenido de texto (alineado a la izquierda)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Nombre
                    Text(
                        text = product!!.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MonkiCafe
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Precio
                    Text(
                        text = "$${String.format("%.2f", product!!.price)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MonkiAmarillo
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Descripción (NUEVO)
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MonkiCafe
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product!!.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MonkiCafe
                    )

                    Spacer(modifier = Modifier.height(32.dp)) // Espacio antes del selector

                    // 6. Selector de cantidad con marco
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(1.dp, MonkiCafe),
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        QuantitySelector(
                            quantity = quantity,
                            onQuantityChange = { newQuantity ->
                                quantity = newQuantity
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Espacio antes del botón

                    // 7. Botón Verde "Agregar al Carrito"
                    Button(
                        onClick = {
                            cartViewModel.addItem(product!!, quantity)
                            Toast.makeText(context, "¡Añadido al carrito!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MonkiAmarillo,
                            contentColor = MonkiCafe
                        )
                    ) {
                        Text("Agregar al Carrito", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Selector de cantidad (no cambia)
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedIconButton(
            onClick = {
                if (quantity > 1) { onQuantityChange(quantity - 1) }
            },
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = MonkiCafe),
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Quitar uno")
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MonkiCafe,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MonkiCafe,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir uno")
        }
    }
}