package com.example.monkibox.admin
import com.example.monkibox.UserViewModel
import com.example.monkibox.login.UserStorage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monkibox.ui.theme.MonkiAmarillo
import com.example.monkibox.ui.theme.MonkiAmarilloSuave
import com.example.monkibox.ui.theme.MonkiCafe
import com.example.monkibox.ui.theme.MonkiFondo

// Usamos @OptIn para el Scaffold y TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    onBackClick: () -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    val userList by viewModel.userList.collectAsState()

    // AÑADIMOS UN SCAFFOLD
    Scaffold(
        containerColor = MonkiFondo,
        topBar = {
            // BARRA SUPERIOR
            TopAppBar(
                title = {
                    // TÍTULO AQUÍ
                    Text("Gestión de Usuarios", color = MonkiCafe, fontWeight = FontWeight.SemiBold)
                },
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
                    containerColor = MonkiAmarillo,
                    navigationIconContentColor = MonkiCafe,
                    titleContentColor = MonkiCafe
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Usamos el padding de Scaffold
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Número de usuarios",
                style = MaterialTheme.typography.titleMedium,
                color = MonkiCafe
            )

            Text(
                text = userList.size.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MonkiCafe
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = MonkiCafe.copy(alpha = 0.5f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ocupa el espacio restante
            ) {
                items(userList) { email ->
                    Text(
                        text = email,
                        fontSize = 18.sp,
                        color = MonkiCafe,
                        fontWeight = FontWeight.Medium
                    )
                    Divider(color = MonkiCafe.copy(alpha = 0.2f))
                }
            }
        }
    }
}