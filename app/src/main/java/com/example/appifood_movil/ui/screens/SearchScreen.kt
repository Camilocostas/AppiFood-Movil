@file:OptIn(ExperimentalFoundationApi::class)

package com.example.appifood_movil.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController
) {
    val criteria by viewModel.criteria.collectAsState()
    val results by viewModel.filteredRestaurants.collectAsState(initial = emptyList())

    // Paleta de colores de AppiFood (de tus fotos)
    val appiFoodRed = Color(0xFFFF4B3A)
    val appiFoodGrayText = Color(0xFF9E9E9E)
    val appiFoodLightGray = Color(0xFFF8F8F8)

    Scaffold(
        // Fondo blanco puro para coincidir con la app
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buscar Restaurantes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1D26)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color(0xFF1A1D26)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp) // Espaciado generoso
                .fillMaxSize()
        ) {


            // --- Lista de Resultados Animada y Estilizada ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio restante
                    .animateContentSize(animationSpec = tween(400)), // Animación suave al filtrar
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = criteria.query,
                        onValueChange = { viewModel.updateQuery(it) },
                        label = { Text("¿Qué se te antoja hoy?", fontSize = 14.sp) },
                        placeholder = {
                            Text("Ej: Comida China, Pizza...", color = appiFoodGrayText.copy(alpha = 0.6f))
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(24.dp), // Muy redondeado (como Categorías)
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = appiFoodGrayText
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = appiFoodRed,
                            cursorColor = appiFoodRed,
                            focusedLabelColor = appiFoodRed,
                            unfocusedBorderColor = appiFoodGrayText.copy(alpha = 0.4f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = appiFoodLightGray
                        )
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    PremiumSliderSection(
                        title = "Distancia máxima",
                        valueLabel = "${criteria.radiusKm.toInt()} km",
                        accentColor = appiFoodRed
                    ) {
                        Slider(
                            value = criteria.radiusKm.toFloat(),
                            onValueChange = { viewModel.updateRadius(it.toDouble()) },
                            valueRange = 1f..10f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = appiFoodRed,
                                activeTrackColor = appiFoodRed,
                                inactiveTrackColor = appiFoodRed.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    PremiumSliderSection(
                        title = "Precio máximo",
                        valueLabel = "$${String.format("%,.0f", criteria.maxPrice)}",
                        accentColor = appiFoodRed
                    ) {
                        Slider(
                            value = criteria.maxPrice.toFloat(),
                            onValueChange = { viewModel.updateMaxPrice(it.toDouble()) },
                            valueRange = 10000f..50000f,
                            steps = 3, // Saltos de 10k: 10, 20, 30, 40, 50
                            colors = SliderDefaults.colors(
                                thumbColor = appiFoodRed,
                                activeTrackColor = appiFoodRed,
                                inactiveTrackColor = appiFoodRed.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
                item {
                        Text(
                            text = "Resultados cercanos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1D26),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                }
                if (results.isEmpty()) {
                    item {
                        EmptySearchResultsPlaceholder()
                    }
                } else {
                    // Usar keys para animar las entradas/salidas de items
                    items(results, key = { it.name }) { restaurant ->
                        RestaurantResultCard(
                            // QUITAMOS el modifier aquí para no causar conflictos
                            restaurant = restaurant,
                            criteria = criteria,
                            accentColor = appiFoodRed,
                            grayTextColor = appiFoodGrayText,
                            navController = navController,
                            // PASAMOS el modifier DE ANIMACIÓN aquí, llamando a la extensión correctamente
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

// --- Componentes Premium de Ayuda ---

@Composable
fun PremiumSliderSection(
    title: String,
    valueLabel: String,
    accentColor: Color,
    slider: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1A1D26)
            )
            // Chip de valor estéticamente como tus CategoryChips
            Surface(
                color = accentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = valueLabel,
                    color = accentColor, // Usamos el rojo de la app
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        slider()
    }
}

@Composable
fun RestaurantResultCard(
    modifier: Modifier = Modifier, // Este es el que recibe el animateItemPlacement()
    restaurant: Restaurant,
    criteria: com.example.appifood_movil.ui.viewmodel.SearchCriteria,
    accentColor: Color,
    grayTextColor: Color,
    navController: NavController
) {
    // Estilo de tarjeta idéntico al de tu Historial de Pedidos
    Card(
        modifier = modifier // <--- ¡AQUÍ ESTÁ EL CAMBIO!
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { navController.navigate("restaurantDetail/${restaurant.name}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(12.dp)), // Bordes redondeados moderno
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1D26)
                )
                Text(
                    restaurant.category,
                    fontSize = 13.sp,
                    color = grayTextColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
                // Estilo de Distancia Premium
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Un "label" de distancia
                        Text(
                            text = "A ${String.format("%.1f", calculateDistance(criteria.userLocation, restaurant))} km",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySearchResultsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.size(70.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No hay restaurantes que coincidan.",
                color = Color.Gray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Intenta ajustar tus filtros.",
                color = Color.Gray.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}

// --- Importante: Función calculateDistance ---
// Asumiendo que la tienes definida en un archivo de utilidades o en la misma SearchScreen.kt
fun calculateDistance(userLocation: android.location.Location?, restaurant: Restaurant): Float {
    if (userLocation == null) return 0f
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,
        restaurant.latitude, restaurant.longitude,
        results
    )
    return results[0] / 1000f // Convertir metros a kilómetros
}