package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.appifood_movil.data.allProducts
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appifood_movil.data.restaurants
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Comidas", "Restaurantes")

    // Define tu color principal (AppiFoodRed) si no lo tienes en el theme
    val appiFoodRed = Color(0xFFFF4B3A)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Favoritos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- PESTAÑAS (TABS) ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color.Gray,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = appiFoodRed,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTabIndex == index) appiFoodRed else Color.Gray,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    )
                }
            }

            // --- CONTENIDO DE CADA PESTAÑA ---
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> FavoriteFoodsTab()
                    1 -> FavoriteRestaurantsTab()
                }
            }
        }
    }
}

@Composable
fun FavoriteFoodsTab() {
    // Usamos los productos reales de tu DataFake
    val favoriteFoods = allProducts.take(4) // Tomamos los primeros 3 como favoritos

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoriteFoods) { product ->
            FoodFavoriteCard(
                foodName = product.name,
                // Como FoodProduct no tiene nombre de restaurante,
                // podemos poner la categoría o un texto fijo
                restaurantName = product.category,
                imageRes = product.imageRes
            )
        }
    }
}

@Composable
fun FoodFavoriteCard(foodName: String, restaurantName: String, imageRes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(restaurantName, color = Color.Gray, fontSize = 12.sp)
            }

            IconButton(onClick = { /* Quitar */ }) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF4B3A))
            }
        }
    }
}

@Composable
fun FavoriteRestaurantsTab() {
    // Usamos los restaurantes reales de tu DataFake
    val favoriteRestaurants = restaurants.filter { it.rating.toDouble() >= 4.8 } // Ejemplo: favoritos los de más de 4.8

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoriteRestaurants) { resto ->
            RestaurantFavoriteCard(
                restaurantName = resto.name,
                description = resto.category,
                imageRes = resto.imageRes
            )
        }
    }
}

@Composable
fun RestaurantFavoriteCard(restaurantName: String, description: String, imageRes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Usamos el mismo icono de favorito para ser consistentes
                Column(modifier = Modifier.weight(1f)) {
                    Text(restaurantName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(description, color = Color.Gray, fontSize = 13.sp)
                }

                IconButton(onClick = { /* Quitar */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF4B3A))
                }
            }
        }
    }
}