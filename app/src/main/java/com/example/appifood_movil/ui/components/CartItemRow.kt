package com.example.appifood_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.data.model.ReceiptItem
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp

@Composable
fun CartItemRow(item: ReceiptItem, viewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp), // Esquinas más redondeadas
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra para efecto "flotante"
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Burger House", fontSize = 12.sp, color = Color.Gray) // Nombre del restaurante pequeño
                Text("$${String.format("%,d", item.price)}",
                    color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold)
            }
            // Controles de cantidad minimalistas
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.decreaseQuantity(item) }) {
                    Icon(Icons.Default.Remove, null, modifier = Modifier.size(18.dp))
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.increaseQuantity(item) }) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}