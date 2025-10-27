package com.example.monkibox.admin
import androidx.compose.foundation.background
import com.example.monkibox.R
import com.example.monkibox.ProductViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.monkibox.ui.theme.MonkiAmarillo
import com.example.monkibox.ui.theme.MonkiCafe
import com.example.monkibox.ui.theme.MonkiFondo
import kotlin.random.Random


// Usamos @OptIn para el Scaffold y TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val context = LocalContext.current

    // --- GESTIÓN DE ESTADOS ---
    val productList by viewModel.productList.collectAsState()
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MonkiFondo,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Productos", color = MonkiCafe, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text(
                text = "Número de productos totales",
                style = MaterialTheme.typography.titleMedium,
                color = MonkiCafe,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = productList.size.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MonkiCafe
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. BOTÓN "Registrar producto"
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonkiAmarillo,
                    contentColor = MonkiCafe // Color del texto dentro del botón
                )
            ) {
                Text("Registrar producto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(productList) { product ->
                    ProductListItem(
                        product = product,
                        onEditClick = { productToEdit = product },
                        onDeleteClick = {
                            viewModel.deleteProduct(product.id)
                        }
                    )
                }
            }
        }
    }

    // --- DIÁLOGOS ---

    // 3. NUEVO DIÁLOGO: Para añadir producto
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newProduct ->
                viewModel.addProduct(newProduct)
                showAddDialog = false
            }
        )
    }

    // Diálogo de Edición (Existente)
    productToEdit?.let { product ->
        EditProductDialog(
            product = product,
            onDismiss = { productToEdit = null },
            onConfirm = { updatedProduct ->
                viewModel.updateProduct(updatedProduct)
                productToEdit = null
            }
        )
    }
}


// --- ÍTEM DE LA LISTA (ACTUALIZADO) ---

@Composable
fun ProductListItem(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) //sombra sutil
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 4. IMAGEN A LA IZQUIERDA (NUEVO)
            // AsyncImage es el Composable de Coil
            AsyncImage(
                model = product.imageUrl, // La URL del producto
                contentDescription = product.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop, // Rellena el espacio
                // Imagen de placeholder mientras carga (¡usa tu mono!)
                placeholder = painterResource(id = R.drawable.mono),
                error = painterResource(id = R.drawable.mono) // Si falla la carga
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de Texto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "ID: ${product.id}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 16.sp
                )
            }

            // Menú de 3 puntitos
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones del producto",
                        tint = MonkiCafe
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MonkiFondo)
                ) {
                    DropdownMenuItem(text = { Text("Editar", color = MonkiCafe) },
                        onClick = {
                        onEditClick()
                        menuExpanded = false
                    })
                    Divider(color = MonkiCafe.copy(alpha = 0.5f))

                    DropdownMenuItem(text = { Text("Eliminar", color = MonkiCafe) },
                        onClick = {
                        onDeleteClick()
                        menuExpanded = false
                    })
                }
            }
        }
    }
}


// --- DIÁLOGO DE EDICIÓN  ---

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var description by remember { mutableStateOf(product.description) }
    // 5. NUEVO CAMPO en el formulario de edición
    var imageUrl by remember { mutableStateOf(product.imageUrl) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MonkiFondo)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Editar Producto", style = MaterialTheme.typography.headlineSmall, color = MonkiCafe)
                Spacer(modifier = Modifier.height(16.dp))

                // Campo URL (NUEVO)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de la Imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = {
                        val newPrice = price.toDoubleOrNull() ?: product.price
                        val newStock = stock.toIntOrNull() ?: product.stock

                        val updatedProduct = product.copy(
                            name = name,
                            price = newPrice,
                            stock = newStock,
                            description = description,
                            imageUrl = imageUrl, // 5. GUARDAR el campo nuevo
                        )
                        onConfirm(updatedProduct)
                    }, colors = ButtonDefaults.buttonColors(containerColor = MonkiAmarillo)
                    ) {
                        Text("Confirmar", color = MonkiCafe, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MonkiCafe
                        )
                    ) { Text("Cerrar", color = Color.White) }
                }
            }
        }
    }
}


// --- 6. ¡NUEVO COMPOSABLE! Diálogo de Registro ---

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    // Estados inicializados como vacíos
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Registrar Producto", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // --- Formulario de Registro ---
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de la Imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = {
                        val newPrice = price.toDoubleOrNull() ?: 0.0
                        val newStock = stock.toIntOrNull() ?: 0

                        // Creamos un producto totalmente NUEVO
                        val newProduct = Product(
                            name = name,
                            price = newPrice,
                            stock = newStock,
                            description = description,
                            imageUrl = imageUrl
                        )
                        onConfirm(newProduct)
                    }, colors = ButtonDefaults.buttonColors(containerColor = MonkiAmarillo)
                    ) {
                        Text("Confirmar", color = MonkiCafe)
                    }

                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MonkiCafe
                        )
                    ) { Text("Cerrar") }
                }
            }
        }
    }
}