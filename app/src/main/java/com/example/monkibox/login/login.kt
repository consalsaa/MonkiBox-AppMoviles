package com.example.monkibox.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.monkibox.R

// Este es tu Composable de la pantalla de Login
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Color(0xFF...) nos permite definir colores exactos
    val backgroundColor = Color(0xFFEFEADC)
    val primaryTextColor = Color(0xFFA75A17)
    val buttonColor = Color(0xFFFBCC05)

    Column(
        modifier = Modifier
            .fillMaxSize()
            // 1. Color de fondo de la pestaña
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.mono),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Magenta)

        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold // (Opcional) Añadimos negrita
        )

        Text(
            text = "🐒🛒MonkiBox!🛒🐒",
            style = MaterialTheme.typography.titleMedium,
            // 4. Color del texto "MonkiBox"
            color = primaryTextColor,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            // 5. Color del botón "Iniciar sesión"
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = primaryTextColor // Color del texto del botón
            )
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onRegisterClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = primaryTextColor
            )
        ) {
            Text("Crear nueva cuenta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BOTÓN DE INVITADO (NUEVO)
        TextButton(
            onClick = { onGuestClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar como Invitado (Solo ver)", color = Color.Gray)
        }
    }
}

// Actualizamos la Preview para que también muestre los estilos
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = { _, _ -> }, onRegisterClick = {}, onGuestClick = {})
}