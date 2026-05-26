@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.appifood_movil.ui.components.AppiFoodFooter
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.appifood_movil.ui.viewmodel.RestaurantDetailViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

// Paleta de colores de AppiFood
val AppiFoodRed = Color(0xFFFF4B3A)
val AppiFoodOrange = Color(0xFFFF5722)
val AppiFoodGrayText = Color(0xFF9E9E9E)
val AppiFoodBackground = Color(0xFFFBFBFB)

@Composable
fun RestaurantDetailScreen(
    navController: NavController, 
    id: Int,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadRestaurant(id)
    }

    var isFavorite by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(1) } // 0=Fotos, 1=Menú, 2=Reseñas

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppiFoodRed)
        }
        return
    }

    val restaurantData = restaurant ?: return

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
                    AsyncImage(
                        model = restaurantData.imageUrl ?: restaurantData.imageRes,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.restaurantechino)
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
                            .padding(horizontal = 16.dp)
                            .padding(top = 54.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            containerColor = Color.White.copy(alpha = 0.5f),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }

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

                    Text(text = restaurantData.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(18.dp))
                        Text(
                            text = " ${restaurantData.rating} (Reviews)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Text(text = "${restaurantData.category} • $$", color = AppiFoodGrayText, fontSize = 15.sp)

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
                             // Aquí podrías mostrar fotos reales si la API las provee
                             Text("No hay fotos adicionales", color = AppiFoodGrayText, modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                }

                1 -> { // PESTAÑA MENÚ
                    if (restaurantData.dishes.isEmpty()) {
                        item {
                            Text(
                                "No hay platos disponibles", 
                                modifier = Modifier.padding(24.dp),
                                color = AppiFoodGrayText
                            )
                        }
                    } else {
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
                    if (restaurantData.reviews.isEmpty()) {
                        item {
                            Text(
                                "No hay reseñas", 
                                modifier = Modifier.padding(24.dp),
                                color = AppiFoodGrayText
                            )
                        }
                    } else {
                        items(restaurantData.reviews) { review ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = review.user,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

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
                                HorizontalDivider(
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
}
