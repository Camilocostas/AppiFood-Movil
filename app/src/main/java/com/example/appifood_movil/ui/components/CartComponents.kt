package com.example.appifood_movil.ui.components

import androidx.compose.foundation.clickable
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
import com.example.appifood_movil.data.model.ReceiptItem

@Composable
fun CartItemCard(name: String, store: String, price: String, imageRes: Int, quantity: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(price, color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrderReceiptScreen(onClose: () -> Unit, restaurantName: String, items: List<ReceiptItem>, paymentMethod: String, totalAmount: String, estimatedTime: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pedido Confirmado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onClose) { Text("Volver al Inicio") }
    }
}

@Composable
fun PaymentOptionRow(name: String, isSelected: Boolean, detailValue: String, onDetailChange: (String) -> Unit, onSelect: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onSelect() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Text(name, modifier = Modifier.padding(start = 8.dp))
    }
}