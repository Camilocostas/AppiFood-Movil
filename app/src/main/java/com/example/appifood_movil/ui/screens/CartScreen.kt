package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    var metodoPago by remember { mutableStateOf("Efectivo") }

    // Datos de pago
    var telefonoNequi by remember { mutableStateOf("") }
    var cuentaBancaria by remember { mutableStateOf("") }
    var emailPaypal by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    val total = 57000

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Pedido", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ---------------- DATOS ----------------
            Text("Datos de entrega", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = celular,
                onValueChange = { celular = it },
                label = { Text("Celular") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            // ---------------- METODOS ----------------
            Text("Método de pago", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            val metodos = listOf("Efectivo", "Nequi", "Bancolombia", "PayPal")

            metodos.forEach { metodo ->
                PaymentOption(
                    metodo = metodo,
                    seleccionado = metodoPago == metodo
                ) {
                    metodoPago = metodo
                }
            }

            // ---------------- FORMULARIOS DINAMICOS ----------------
            AnimatedContent(
                targetState = metodoPago,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                }
            ) { metodo ->

                Column {
                    Spacer(modifier = Modifier.height(15.dp))

                    when (metodo) {

                        "Nequi" -> {
                            OutlinedTextField(
                                value = telefonoNequi,
                                onValueChange = { telefonoNequi = it },
                                label = { Text("Número Nequi") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "Bancolombia" -> {
                            OutlinedTextField(
                                value = cuentaBancaria,
                                onValueChange = { cuentaBancaria = it },
                                label = { Text("Número de cuenta") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "PayPal" -> {
                            OutlinedTextField(
                                value = emailPaypal,
                                onValueChange = { emailPaypal = it },
                                label = { Text("Correo PayPal") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "Efectivo" -> {
                            Text(
                                "Pagarás en efectivo al recibir el pedido",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ---------------- TOTAL ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:")
                Text(
                    "$$total",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF4B3A),
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- BOTON ----------------
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("FINALIZAR COMPRA", color = Color.White)
            }
        }
    }

    // ---------------- DIALOGO ----------------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("home")
                }) {
                    Text("OK")
                }
            },
            title = { Text("Compra realizada 🎉") },
            text = {
                Text("Pago con $metodoPago confirmado")
            }
        )
    }
}

@Composable
fun PaymentOption(
    metodo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                if (seleccionado) Color(0xFFFFE5E0) else Color(0xFFF5F5F5),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = if (seleccionado) 2.dp else 0.dp,
                color = Color(0xFFFF4B3A),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Payment,
            contentDescription = null,
            tint = if (seleccionado) Color(0xFFFF4B3A) else Color.Gray
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = metodo,
            fontWeight = FontWeight.Medium,
            color = if (seleccionado) Color(0xFFFF4B3A) else Color.Black
        )
    }
}