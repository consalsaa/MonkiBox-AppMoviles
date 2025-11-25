package com.example.monkibox.usuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monkibox.R

@Composable
fun MoreScreen(
    userEmail: String,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val monkiBrown = Color(0xFF9B6A3F)
    val monkiBackground = Color(0xFFEFEADC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(monkiBackground)
    ) {
        // CABECERA (Header Café)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(monkiBrown)
                .padding(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de perfil (Logo MonkiBox)
                Image(
                    painter = painterResource(id = R.drawable.mono), // Tu logo
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White) // Un bordecito blanco
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Información del usuario
                Column {
                    Text(
                        text = userEmail,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Nivel: Monki Experto", // Detalle estético
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // LISTA DE OPCIONES
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Opción: Inicio
            MenuItem(
                icon = Icons.Default.Home,
                text = "Inicio",
                onClick = onHomeClick
            )

            // Opción: Historial
            MenuItem(
                icon = Icons.Default.ReceiptLong,
                text = "Historial de compras",
                onClick = onHistoryClick
            )

            // Opción: Acerca de
            MenuItem(
                icon = Icons.Default.Info,
                text = "Acerca de MonkiBox",
                onClick = onAboutClick
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Opción: Cerrar Sesión (Importante para usabilidad)
            MenuItem(
                icon = Icons.Default.Logout,
                text = "Cerrar sesión",
                textColor = Color.Red,
                iconColor = Color.Red,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Gray,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        // Flechita a la derecha (estilo MercadoLibre)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}