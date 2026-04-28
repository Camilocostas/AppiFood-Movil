package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.components.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun CartScreen(viewModel: CartViewModel = viewModel(), navController: NavController) {
    val cartItems = viewModel.cartItems
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val shipping = 3500
    val total = subtotal + shipping

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi carrito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = { cartItems.clear() }) {
                        Text("Vaciar", color = Color(0xFFFF4B3A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    CartItemRow(item, viewModel)
                }
            }

            // Sección de Cupón
            OutlinedTextField(
                value = "", onValueChange = {},
                placeholder = { Text("Código de cupón (ej: BIENVENIDO)") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                trailingIcon = { TextButton(onClick = {}) { Text("Aplicar", color = Color(0xFFFF4B3A)) } }
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Filas de resumen
                    SummaryRow("Subtotal", "$${String.format("%,d", subtotal)}")
                    SummaryRow("Domicilio", "$${String.format("%,d", shipping)}")
                    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Total", "$${String.format("%,d", total)}", isTotal = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A))
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Confirmar pedido")
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isTotal) Color(0xFFFF4B3A) else Color.Black
        )
    }
}