package com.example.appifood_movil.ui.components

import android.location.Location
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    viewModel: SearchViewModel,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val criteria by viewModel.criteria.collectAsState()
    val results by viewModel.filteredRestaurants.collectAsState(initial = emptyList())
    val appiFoodRed = Color(0xFFFF4B3A)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE0E0E0)) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Filtrar búsqueda",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1D26),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = criteria.query,
                onValueChange = { viewModel.updateQuery(it) },
                label = { Text("¿Qué se te antoja hoy?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appiFoodRed,
                    cursorColor = appiFoodRed,
                    focusedLabelColor = appiFoodRed
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Secciones con sliders
            FilterSliderSection("Distancia máxima", "${criteria.radiusKm.toInt()} km", appiFoodRed) {
                Slider(
                    value = criteria.radiusKm.toFloat(),
                    onValueChange = { viewModel.updateRadius(it.toDouble()) },
                    valueRange = 1f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = appiFoodRed, activeTrackColor = appiFoodRed)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FilterSliderSection("Precio máximo", "$${String.format("%,.0f", criteria.maxPrice)}", appiFoodRed) {
                Slider(
                    value = criteria.maxPrice.toFloat(),
                    onValueChange = { viewModel.updateMaxPrice(it.toDouble()) },
                    valueRange = 10000f..50000f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = appiFoodRed, activeTrackColor = appiFoodRed)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 350.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (results.isEmpty()) {
                    item { Text("No hay restaurantes que coincidan.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp)) }
                } else {
                    items(results, key = { it.name }) { restaurant ->
                        RestaurantResultItem(restaurant, criteria, appiFoodRed, navController, onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSliderSection(title: String, valueLabel: String, accentColor: Color, slider: @Composable () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(valueLabel, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        slider()
    }
}

@Composable
fun RestaurantResultItem(
    restaurant: Restaurant,
    criteria: com.example.appifood_movil.ui.viewmodel.SearchCriteria,
    accentColor: Color,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                navController.navigate("restaurantDetail/${restaurant.name}")
                onDismiss()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(restaurant.category, fontSize = 13.sp, color = Color.Gray)
                Text(
                    "A ${String.format("%.1f", calculateDistance(criteria.userLocation, restaurant))} km",
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun calculateDistance(userLocation: Location?, restaurant: Restaurant): Float {
    if (userLocation == null) return 0f
    val results = FloatArray(1)
    Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,
        restaurant.latitude, restaurant.longitude,
        results
    )
    return results[0] / 1000f
}
