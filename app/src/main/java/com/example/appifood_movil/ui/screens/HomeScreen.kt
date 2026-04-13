package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appifood_movil.ui.viewmodel.HomeViewModel
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.components.CategoryChip
import com.example.appifood_movil.data.allProducts
import com.example.appifood_movil.data.restaurants
import com.example.appifood_movil.ui.components.PromoBanner
import com.example.appifood_movil.ui.components.CarouselHeader
import com.example.appifood_movil.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(), // Aseguramos que el Scaffold ocupe todo
        bottomBar = {
            AppiFoodFooter(
                navController = navController,
                currentRoute = "home",
                cartCount = viewModel.cartCount
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    CarouselHeader(height = 300.dp) {
                        // Aquí dibujamos lo que va encima del carrusel
                        Image(
                            painter = painterResource(id = R.drawable.logomau), // Tu logo
                            contentDescription = null,
                            modifier = Modifier.width(130.dp).wrapContentHeight()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Popayán, Cauca",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text("Hola, Camilo", color = Color.White, fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }

                item {
                    PromoBanner(onClick = {
                        navController.navigate("home")
                    })
                }

                // 2. Sección Categorías
                item {
                    SectionHeader(title = "Categorías")
                    val categories = listOf("Todos", "Rapida", "Oriental", "Mexicana", "China", "Vegetariana")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected =
                                (cat == "Todos" && viewModel.selectedCategory == "Todas") || viewModel.selectedCategory == cat
                            CategoryChip(text = cat, isSelected = isSelected) {
                                viewModel.onCategorySelected(cat)
                            }
                        }
                    }
                }

                // 3. SECCIÓN: COMIDAS EN PROMOCIÓN HOY (Ahora de primero)
                item { SectionHeader(title = "Promociones de Hoy", showViewAll = true) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        // Tomamos los primeros productos para mostrar como promoción
                        items(allProducts) { product ->
                            PromoFoodCard(
                                name = product.name,
                                price = product.price,
                                oldPrice = "$35.000", // Precio ficticio para el ejemplo
                                imageRes = product.imageRes,
                                onNavigate = {
                                    navController.navigate("productDetail/${product.name}/${product.price}/${product.imageRes}")
                                }
                            )
                        }
                    }
                }

// --- SECCIÓN: RESTAURANTES POPULARES (Usando tu lista restaurants) ---
                item { SectionHeader(title = "Restaurantes Populares", showViewAll = true) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(restaurants) { restaurant ->
                            MinimalRestaurantCard(
                                name = restaurant.name,
                                rating = restaurant.rating,
                                time = "25-40 min", // Puedes agregar este campo a tu modelo Restaurant luego
                                imageRes = restaurant.imageRes,
                                onClick = {
                                    navController.navigate("restaurantDetail/${restaurant.name}")
                                }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}
// --- Componentes Modernos y Minimalistas ---

@Composable
fun PromoFoodCard(name: String, price: String, oldPrice: String, imageRes: Int, onNavigate: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onNavigate() }
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp)), // Bordes más redondeados
                contentScale = ContentScale.Crop
            )
            // Badge de descuento minimalista
            Surface(
                color = Color(0xFFFF4B3A),
                shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 12.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    "OFERTA",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(price, color = Color(0xFFFF4B3A), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                oldPrice,
                color = Color.Gray,
                fontSize = 11.sp,
                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
            )
        }
    }
}

@Composable
fun MinimalRestaurantCard(
    name: String,
    rating: String,
    time: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    // Card estilo "Flat" (sin sombras, fondo gris suave)
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF8F8F8))
            .clickable { onClick() } // <--- TE FALTABA ESTA LÍNEA
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
            Text(" $rating", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(" • $time", fontSize = 11.sp, color = Color.Gray)
        }
    }
}
@Composable
fun SectionHeader(
    title: String,
    showViewAll: Boolean = false,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1D26) // Un negro más moderno y suave
        )
        if (showViewAll) {
            Text(
                text = "Ver todos",
                color = Color(0xFFFF4B3A), // El rojo de AppiFood
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}