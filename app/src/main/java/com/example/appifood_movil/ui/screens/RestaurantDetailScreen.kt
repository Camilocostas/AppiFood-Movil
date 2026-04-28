package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.appifood_movil.R
import com.example.appifood_movil.data.restaurants
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.PaddingValues
import com.example.appifood_movil.ui.components.SwipeActionButton
@Composable
fun RestaurantDetailScreen(navController: NavController, name: String?) {
    val restaurant = restaurants.find { it.name == name }
    var isFavorite by remember { mutableStateOf(false) }

    // Lista simulada de imágenes para la galería
    val galleryImages = listOf(
        R.drawable.burger_background_2,
        R.drawable.burger_background_2,
        R.drawable.burger_background_2,
        R.drawable.burger_background_2
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // 1. CONTENIDO CON SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp) // Espacio para que el contenido no quede oculto tras el botón
                .verticalScroll(rememberScrollState())
        ) {
            // --- CABECERA (Solo una vez) ---
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Image(
                    painter = painterResource(id = restaurant?.imageRes ?: R.drawable.burger_background_2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- INFO DEL RESTAURANTE ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // 1. Etiqueta de descuento (10% OFF)
                Surface(
                    color = Color(0xFFFF4B3A).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color(0xFFFF4B3A), modifier = Modifier.size(16.dp))
                        Text(" 10% OFF", color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // 2. Nombre y Calificación
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = name ?: "Restaurant", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                        Text(" 4.8 (567 reseñas)", fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }

                // 3. Categoría y Precio
                Text(text = "Italian, Mediterranean • $$", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp))

                // 4. Ubicación con icono
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF4B3A), modifier = Modifier.size(20.dp))
                    Text(text = " " + (restaurant?.address ?: "Dirección"), color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                // 5. Tabs
                TabRow(
                    selectedTabIndex = 2,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[2]),
                            color = Color(0xFFFF4B3A)
                        )
                    }
                ) {
                    listOf("Acerca de", "Menu", "Galeria", "Reseña").forEachIndexed { index, title ->
                        Tab(
                            selected = index == 2,
                            onClick = {},
                            text = {
                                Text(
                                    title,
                                    color = if(index == 2) Color(0xFFFF4B3A) else Color.Gray,
                                    fontWeight = if(index == 2) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
// Datos de prueba para el collage (puedes usar imágenes de tus platos o restaurante)
// Usaremos alturas diferentes para simular el efecto mosaico
            val collageItems = listOf(
                Pair(R.drawable.arrozchaufa, 220.dp), // Imagen alta
                Pair(R.drawable.lomosaltado, 140.dp),   // Imagen baja
                Pair(R.drawable.tallarinsaltarin, 140.dp),// Imagen baja
                Pair(R.drawable.restaurantechino, 140.dp) // Imagen alta
            )

// Usamos StaggeredGrid para el efecto mosaico elegante
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2), // 2 columnas
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // Altura fija para que el scroll principal funcione
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(collageItems) { item ->
                    Image(
                        painter = painterResource(id = item.first),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(item.second) // Altura variable del Pair
                            .clip(RoundedCornerShape(16.dp)), // Bordes muy redondeados para toque moderno
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
// --- FIN DE LA SECCIÓN DE GALERÍA COLLAGE ---

            // --- MAPA ---
            Spacer(modifier = Modifier.height(20.dp))
            Text("Ubicación", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp))

            val restaurantLocation = LatLng(restaurant?.latitude ?: 2.4435, restaurant?.longitude ?: -76.6063)
            GoogleMap(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(24.dp).clip(RoundedCornerShape(15.dp)),
                cameraPositionState = rememberCameraPositionState { position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(restaurantLocation, 15f) }
            ) {
                Marker(state = MarkerState(position = restaurantLocation))
            }

            }
    }
}




@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color = Color(0xFFFF4B3A)
) {
    Surface(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F1F1))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DishItem(name: String, price: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    color = Color(0xFFFF4B3A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}