package com.example.monkibox.usuario
import androidx.compose.foundation.background
import com.example.monkibox.R
import com.example.monkibox.ProductViewModel
import com.example.monkibox.admin.Product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.monkibox.ui.theme.MonkiAmarilloSuave
import com.example.monkibox.ui.theme.MonkiCafe

// import com.tuproyecto.R // (Asegúrate de tener R.drawable.mono)

@Composable
fun ProductsScreen(
    // 1. Obtenemos el ViewModel (igual que en Admin)
    viewModel: ProductViewModel,
    // 2. Parámetro para la navegación al detalle
    onProductClick: (Product) -> Unit
) {
    // 3. Observamos la lista de productos del ViewModel
    val productList by viewModel.productList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.monkibox.ui.theme.MonkiFondo)
            .padding(16.dp)
    ) {
        // 4. Título de la pantalla
        Text(
            text = "Productos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MonkiCafe,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 5. La Cuadrícula (Grid) Scrolleable
        LazyVerticalGrid(
            // Define 2 columnas
            columns = GridCells.Fixed(2),
            // Espacio entre los ítems
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            // Padding para que no se pegue a los bordes
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 'items' crea una tarjeta por cada producto en la lista
            items(productList) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

// --- NUEVO COMPOSABLE: La tarjeta de producto ---

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Hacemos la tarjeta clickeable
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 1. Imagen (con Coil)
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MonkiAmarilloSuave),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.mono),
                error = painterResource(id = R.drawable.mono)
            )

            // Contenedor para el texto
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 2. Nombre
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, // Evita que nombres largos rompan el diseño
                    overflow = TextOverflow.Ellipsis,
                    color = MonkiCafe
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Precio
                Text(
                    text = "$${String.format("%.2f", product.price)}", // Formateado
                    style = MaterialTheme.typography.bodyMedium,
                    color = MonkiCafe
                )
            }
        }
    }
}