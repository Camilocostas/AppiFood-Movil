package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)

    // Estados para controlar los modales
    val sheetState = rememberModalBottomSheetState()
    var showPaySheet by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("Efectivo") }
    // Dentro de CartScreen, junto a tus otros estados
    var paymentDetail by remember { mutableStateOf("") }

    // Datos simulados de la compra para el recibo
    val restaurantName = "Burger House"
    val items = listOf(
        ReceiptItem("Delicious Burger", 1, "$35.000"),
        ReceiptItem("Chicago Deep Pizza", 2, "$44.000") // 2 pizzas a $22.000 c/u
    )
    val totalAmount = "$82.500" // Subtotal $79k + $3.5k domicilio
    val estimatedTime = "25 - 35 min"

    // 1. Lógica de Pantalla: O mostramos el recibo animado o el carrito
    if (showReceipt) {
        OrderReceiptScreen(
            onClose = {
                showReceipt = false
                navController.navigate("home")
            },
            restaurantName = restaurantName,
            items = items,
            paymentMethod = if (paymentDetail.isNotEmpty()) "$selectedPayment ($paymentDetail)" else selectedPayment,
            totalAmount = totalAmount,
            estimatedTime = estimatedTime
        )
    } else {
        // 2. Contenido del Carrito (Solo se ve si showReceipt es false)
        Scaffold { padding ->
            // ... (Mantenemos tu código original del Scaffold y carrito) ...
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // --- CABECERA ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi carrito", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Vaciar", color = appiFoodRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                // --- LISTA ---
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        CartItemCard("Delicious Burger", "Burger House", "$35.000", R.drawable.bicmac, 1)
                        Spacer(modifier = Modifier.height(16.dp))
                        CartItemCard("Chicago Deep Pizza", "Pizza Nostra", "$22.000", R.drawable.arrozchaufa, 2)
                    }
                }

                // --- RESUMEN DE PAGO ---
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    SummaryRow("Subtotal", "$79.000")
                    SummaryRow("Domicilio", "$3.500")
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF5F5F5))
                    Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$82.500", color = appiFoodRed, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }

                // BOTÓN CONFIRMAR: Abre el Modal de Pago
                Button(
                    onClick = { showPaySheet = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appiFoodRed),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 3. Modal de Pago (Fuera del else para que pueda aparecer sobre el Scaffold)
    if (showPaySheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaySheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth().padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Método de pago", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                val methods = listOf("Efectivo", "Nequi", "Daviplata", "Bancolombia", "PayPal")
                methods.forEach { method ->
                    PaymentOptionRow(
                        name = method,
                        isSelected = selectedPayment == method,
                        detailValue = paymentDetail, // <--- Pasamos el estado actual
                        onDetailChange = { paymentDetail = it }, // <--- Actualizamos el estado al escribir
                        onSelect = {
                            selectedPayment = method
                            paymentDetail = ""
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total a pagar:", fontWeight = FontWeight.Medium)
                    Text("$82.500", color = appiFoodRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Button(
                    onClick = {
                        showPaySheet = false
                        showReceipt = true // Al volverse true, el IF de arriba cambiará la pantalla al QR
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appiFoodRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Pagar ahora", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Data class para los items del recibo
data class ReceiptItem(val name: String, val quantity: Int, val totalPrice: String)

@Composable
fun OrderReceiptScreen(
    onClose: () -> Unit,
    restaurantName: String,
    items: List<ReceiptItem>,
    paymentMethod: String,
    totalAmount: String,
    estimatedTime: String
) {
    val appiFoodRed = Color(0xFFFF4B3A)

    // --- ANIMACIONES DE CELEBRACIÓN ---
    // 1. Animación de escala (zoom out) para la tarjeta
    val scale = remember { Animatable(0.5f) }
    // 2. Animación de rotación para los confetis
    val infiniteTransition = rememberInfiniteTransition(label = "Confetti Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "RotationAngle"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(
        modifier = Modifier.fillMaxSize().background(appiFoodRed).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- Confetis Animados de Fondo ---
        repeat(15) { index ->
            Icon(
                Icons.Default.Star, contentDescription = null,
                tint = listOf(Color(0xFFFFD700), Color.White, Color(0xFFFFFACD)).random(),
                modifier = Modifier
                    .size(listOf(10.dp, 15.dp, 20.dp).random())
                    .offset(x = listOf(-150.dp, 150.dp, 0.dp).random(), y = listOf(-250.dp, 250.dp, 0.dp).random())
                    .rotate(rotation * (index % 3 + 1))
            )
        }

        // --- LA TARJETA DEL RECIBO (Con Animación de Escala) ---
        Card(
            modifier = Modifier.fillMaxWidth().scale(scale.value),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- CABECERA ---
                Text("Detalles de la compra", color = Color.Gray, fontSize = 14.sp)
                Text(restaurantName, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Status: ● Confirmado", color = Color(0xFF4CAF50), fontSize = 12.sp)

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

                // --- SECCIÓN: PLATOS PEDIDOS ---
                Text("Tus platos", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.quantity}x ${item.name}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(item.totalPrice, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

                // --- SECCIÓN: RESUMEN DE PAGO ---
                ReceiptInfoRow("Forma de pago", paymentMethod)
                ReceiptInfoRow("Total de la compra", totalAmount, isBold = true, isRed = true)

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

                // --- SECCIÓN: TIEMPO DE ENTREGA ---
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(appiFoodRed.copy(alpha = 0.1f)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsBike, null, tint = appiFoodRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Tiempo estimado de llegada:", color = Color.Gray, fontSize = 12.sp)
                        Text(estimatedTime, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = appiFoodRed)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- CÓDIGO QR ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    modifier = Modifier.size(140.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.Black)
                    }
                }
                Text("Escanea el código al recibir el pedido", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN: VOLVER AL INICIO
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = appiFoodRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Volver al Inicio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Componentes Reutilizables para el Recibo
@Composable
fun ReceiptInfoRow(label: String, value: String, isBold: Boolean = false, isRed: Boolean = false) {
    val appiFoodRed = Color(0xFFFF4B3A)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(
            value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isRed) appiFoodRed else Color.Black,
            fontSize = if (isBold) 16.sp else 14.sp
        )
    }
}

// Mantenemos tus componentes originales (CartItemCard, SummaryRow, PaymentOptionRow)
@Composable
fun CartItemCard(name: String, store: String, price: String, imageRes: Int, quantity: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(store, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(price, color = Color.Red, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.RemoveCircleOutline, null, tint = Color.LightGray)
                }
                Text(quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                IconButton(onClick = {}, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.AddCircle, null, tint = Color(0xFFFF4B3A))
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PaymentOptionRow(
    name: String,
    isSelected: Boolean,
    detailValue: String, // <--- Nuevo
    onDetailChange: (String) -> Unit, // <--- Nuevo
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFFFE5E0) else Color(0xFFF5F5F5))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF4B3A))
            )
            Text(name, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
        }

        // --- ANIMACIÓN DE DESPLIEGUE ---
        AnimatedVisibility(
            visible = isSelected && name != "Efectivo",
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp)) {
                val placeholder = when (name) {
                    "Nequi", "Daviplata" -> "Número de celular"
                    "PayPal" -> "Correo de PayPal"
                    "Bancolombia" -> "Número de cuenta"
                    else -> ""
                }

                OutlinedTextField(
                    value = detailValue, // <--- Ahora usa el estado
                    onValueChange = onDetailChange, // <--- Actualiza el estado
                    placeholder = { Text(placeholder, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    // Agregamos el teclado numérico para Nequi/Daviplata
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = if (name == "PayPal") androidx.compose.ui.text.input.KeyboardType.Email
                        else androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF4B3A),   // Color del borde al tocarlo
                        unfocusedBorderColor = Color.LightGray,  // Color del borde en reposo
                        focusedContainerColor = Color.White,     // Fondo blanco
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        }
    }
}