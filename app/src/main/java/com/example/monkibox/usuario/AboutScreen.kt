package com.example.monkibox.usuario

import com.example.monkibox.viewmodels.AboutViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.monkibox.viewmodels.FakeReview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    viewModel: AboutViewModel = viewModel() // Inyectamos el ViewModel
) {
    val reviews by viewModel.reviews.collectAsState() // Observamos la lista
    val monkiBackground = Color(0xFFEFEADC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de MonkiBox") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = monkiBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(monkiBackground)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Scroll por si las reseñas son largas
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // INFO DE LA EMPRESA
            Text(
                text = "MonkiBox🐒",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA75A17)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Somos la tienda líder en productos random y divertidos. Nuestra misión es llevar una sonrisa a tu puerta con cada caja. 🙊❤️",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN DE RESEÑAS
            Text(
                text = "Lo que dicen nuestros clientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "(Reseñas verificadas por RandomUser API)",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Si la lista está vacía, mostramos carga. Si no, mostramos las reseñas
            if (reviews.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Renderizamos cada reseña
                reviews.forEach { review ->
                    ReviewCard(review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: FakeReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Foto de perfil redonda
            AsyncImage(
                model = review.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                // Nombre y País
                Text(text = review.name, fontWeight = FontWeight.Bold)
                Text(text = review.country, fontSize = 12.sp, color = Color.Gray)

                // Estrellas
                Row {
                    repeat(review.stars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107), // Color dorado
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // El comentario
                Text(text = "\"${review.comment}\"", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}