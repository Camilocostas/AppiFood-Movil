@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.R
import com.example.appifood_movil.data.restaurants
import com.example.appifood_movil.ui.components.AppiFoodFooter
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.lazy.items
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

// Paleta de colores de AppiFood (de tus fotos)
val AppiFoodRed = Color(0xFFFF4B3A)
val AppiFoodOrange = Color(0xFFFF5722)
val AppiFoodGrayText = Color(0xFF9E9E9E)
val AppiFoodBackground = Color(0xFFFBFBFB)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RestaurantDetailScreen(navController: NavController, name: String?) {
    val restaurantData = restaurants.find { it.name == name } ?: restaurants.first()
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(1) } // 0=Menú, 1=Fotos, 2=Reseñas

    Scaffold(
        bottomBar = {
            AppiFoodFooter(navController, "home", 6, {})
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(AppiFoodBackground)
        ) {
            // CABECERA
            item {
                Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                    Image(
                        painter = painterResource(id = restaurantData.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(0.5f)
                                )
                            )
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) // Primero el horizontal
                            .padding(top = 54.dp, bottom = 20.dp), // Luego el vertical
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Atrás (FloatingActionButton)
                        FloatingActionButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            containerColor = Color.White.copy(alpha = 0.5f),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }

                        // Botón Favorito (FloatingActionButton)
                        FloatingActionButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            containerColor = Color.White.copy(alpha = 0.5f),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Black
                            )
                        }
                    }
                }
            }

            // CONTENIDO BLANCO
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp, bottom = 12.dp)
                ) {
                    // A. Distintivo de descuento (Si aplica)
                    Surface(
                        color = AppiFoodOrange.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "10% OFF",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = AppiFoodOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // B. Título
                    Text(text = restaurantData.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)

                    // C. Rating y Reviews (La info que faltaba)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(18.dp))
                        Text(
                            text = " ${restaurantData.rating} (567 reviews)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // D. Categoría y Precio
                    Text(text = "Italian, Mediterranean • $$", color = AppiFoodGrayText, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.White,
                        contentColor = AppiFoodOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(
                                    tabPositions[selectedTabIndex]
                                ), color = AppiFoodOrange
                            )
                        }
                    ) {
                        listOf("Fotos", "Menu", "Reseñas").forEachIndexed { index, title ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        title,
                                        color = if (index == selectedTabIndex) AppiFoodOrange else AppiFoodGrayText
                                    )
                                })
                        }
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> { // PESTAÑA FOTOS
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-40).dp)
                                .padding(horizontal = 24.dp)
                                .background(Color.White)
                                .padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("(5 Fotos)", color = AppiFoodGrayText)
                                Text(
                                    "Ver Fotos",
                                    color = AppiFoodOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().height(340.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ImageResultCard(R.drawable.arrozchaufa, Modifier.weight(1.5f))
                                    ImageResultCard(
                                        R.drawable.restaurantechino,
                                        Modifier.weight(1f)
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ImageResultCard(R.drawable.lomosaltado, Modifier.weight(1f))
                                    ImageResultCard(
                                        R.drawable.tallarinsaltarin,
                                        Modifier.weight(1.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> { // PESTAÑA MENÚ
                    items(restaurantData.dishes) { dish ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = dish.imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = dish.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "$${String.format("%.0f", dish.price)}",
                                    color = AppiFoodOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Ubicación del Restaurante",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Usamos las coordenadas del modelo restaurantData
                            val restaurantLocation = LatLng(restaurantData.latitude, restaurantData.longitude)
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(restaurantLocation, 16f)
                            }

                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                cameraPositionState = cameraPositionState,
                                uiSettings = MapUiSettings(zoomControlsEnabled = false)
                            ) {
                                Marker(
                                    state = MarkerState(position = restaurantLocation),
                                    title = restaurantData.name,
                                    snippet = restaurantData.address
                                )
                            }
                        }
                    }

                }

                2 -> { // PESTAÑA RESEÑAS
                    items(restaurantData.reviews) { review ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = review.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                // Estrellas dinámicas
                                Row {
                                    repeat(review.rating) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB800),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = review.comment,
                                color = AppiFoodGrayText,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Divider(
                                modifier = Modifier.padding(top = 12.dp),
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente helper para las tarjetas de imagen en mosaico
@Composable
fun ImageResultCard(imageRes: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
