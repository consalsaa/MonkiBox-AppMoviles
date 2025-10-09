package com.example.monkibox
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monkibox.ui.theme.MonkiBoxTheme
import org.w3c.dom.Text
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonkiBoxTheme {
                PantallaPefil()
            }
        }
    }
}

@Composable
fun PantallaPefil(){
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var clave2 by remember { mutableStateOf("") }
    var guardado by remember { mutableStateOf(false) }

    LaunchedEffect(guardado) {
        if (guardado) {
            kotlinx.coroutines.delay(3000)
            guardado = false
        }
    }

    fun clearForm() {
        usuario = ""
        correo = ""
        clave = ""
        clave2 = ""
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFEFEADC)
    ) {
        Column (
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.mono),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Magenta)

            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("¡Bienvenid@ $usuario", fontSize = 24.sp, color = Color(0xFFA75A17))
            Text("🐒🛒MonkiBox!🛒🐒", fontSize = 24.sp, color = Color(0xFFA75A17))

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // ---------- CAMPO USUARIO ----------
            TextField(
                value = usuario,
                onValueChange = {usuario= it},
                label = {Text("Nombre")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ---------- CAMPO CORREO ----------
            TextField(
                value = correo,
                onValueChange = {correo = it},
                label = {Text("Correo")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ---------- CAMPO CONTRASEÑA ----------
            TextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = clave2,
                onValueChange = { clave2 = it },
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---------- BOTON GUARDADO ----------
            Button(
                onClick = {guardado = true},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBCC05),
                    contentColor = Color.Black
                )
            ) {
                Text("Guardar Cambios")
            }

            // ---------- BOTON LIMPIAR ----------
            Button(
                onClick = { clearForm() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA75A17),
                    contentColor = Color.White
                )
            ) {
                Text("Limpiar formulario")
            }

            if(guardado){
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tu perfil se ha guardado",
                    color = Color(0xFF000000),
                    fontSize = 16.sp


                )
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonkiBoxTheme {
        PantallaPefil()
    }
}