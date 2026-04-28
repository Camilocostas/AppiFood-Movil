package com.example.appifood_movil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image

@Composable
fun CartItemCard(
    dishName: String,
    storeName: String,
    price: Int, // Usaremos String como viene en tu modelo Dish
    imageRes: Int,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen del producto
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(dishName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(storeName, fontSize = 12.sp, color = Color.Gray)
                Text("$${price}", color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold)
            }

            // Controles
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) { Text("-") }
                Text("$quantity", fontWeight = FontWeight.Bold)
                IconButton(onClick = onIncrease) { Text("+") }
            }
        }
    }
}

@Composable
fun OrderReceiptScreen(onClose: () -> Unit, restaurantName: String, totalAmount: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pedido Confirmado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Total pagado: $totalAmount")
        Button(onClick = onClose) { Text("Volver al Inicio") }
    }
}