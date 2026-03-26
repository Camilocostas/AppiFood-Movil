package com.example.appifood_movil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appifood_movil.data.model.Restaurant

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = restaurant.name, style = MaterialTheme.typography.titleMedium)
            Text(text = restaurant.category, style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "⭐ ${restaurant.rating}")
                Text(text = "🕒 ${restaurant.deliveryTime}")
            }
        }
    }
}
