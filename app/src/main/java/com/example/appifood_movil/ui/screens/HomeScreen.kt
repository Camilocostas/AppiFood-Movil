package com.example.appifood_movil.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.components.CarouselHeader
import com.example.appifood_movil.ui.viewmodel.HomeViewModel
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel
) {
    val restaurants by viewModel.restaurants.collectAsState()
    val filteredRestaurants by searchViewModel.filteredRestaurants.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val promotionProducts by viewModel.promotionProducts.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val background = colorScheme.background
    val surface = colorScheme.surface
    val onSurface = colorScheme.onSurface
    val onSurfaceVariant = colorScheme.onSurfaceVariant
    val primary = colorScheme.primary

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            searchViewModel.fetchUserLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // ─── ANIMACIÓN DE ENTRADA (ESTRAMBÓTICA Y ELEGANTE) ───
    FadeInEntrance  {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AppiFoodFooter(
                    navController = navController,
                    currentRoute = Screen.Home.route,
                    cartCount = 6,
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // ── CARRUSEL HEADER ────────────────────────────────────
                item {
                    CarouselHeader(height = 300.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp, start = 20.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logomau),
                                contentDescription = null,
                                modifier = Modifier.width(130.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(id = R.string.default_location),
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.welcome_user),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "¿Qué antojo tienes hoy? 🍔",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // ── PROMO BANNER ──────────────────────────────────────
                item {
                    AnimatedPromoBanner(
                        onClick = { /* Navegar a promociones */ },
                        primary = primary
                    )
                }

                // ── CATEGORÍAS ─────────────────────────────────────────
                item {
                    AnimatedSectionHeader(
                        title = stringResource(id = R.string.section_categories),
                        onSurface = onSurface,
                        primary = primary
                    )
                    val categories = listOf("Todos", "Bebidas", "Postres", "Rapida", "Oriental", "Mexicana", "Vegetariana")
                    val selectedCategory by viewModel.selectedCategory.collectAsState()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = if (cat == "Todos") {
                                selectedCategory == "Todos"
                            } else {
                                selectedCategory == cat
                            }
                            AnimatedCategoryChip(
                                text = cat,
                                isSelected = isSelected,
                                onClick = { viewModel.onCategorySelected(cat) },
                                primary = primary,
                                onSurface = onSurface,
                                surface = surface
                            )
                        }
                    }
                }

                // ── PROMOCIONES ─────────────────────────────────────────
                item {
                    AnimatedSectionHeader(
                        title = stringResource(id = R.string.section_promotions),
                        showViewAll = true,
                        onViewAllClick = { /* TODO */ },
                        onSurface = onSurface,
                        primary = primary
                    )
                }

                item {
                    val filteredPromotions by viewModel.filteredPromotions.collectAsState()

                    if (filteredPromotions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay promociones en esta categoría",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(filteredPromotions) { product ->
                                AnimatedPromoFoodCard(
                                    name = product.name,
                                    price = "$ ${String.format("%,.0f", product.precioPromocion)}",
                                    oldPrice = "$ ${String.format("%,.0f", product.price)}",
                                    imageUrl = product.imagenUrl,
                                    imageRes = product.imageRes,
                                    discount = product.descuento,
                                    onNavigate = {
                                        navController.navigate(Screen.ProductDetail.passId(product.id))
                                    },
                                    surface = surface,
                                    onSurface = onSurface,
                                    primary = primary
                                )
                            }
                        }
                    }
                }

                // ── RESTAURANTES POPULARES ────────────────────────────
                item {
                    AnimatedSectionHeader(
                        title = stringResource(id = R.string.section_popular_restaurants),
                        showViewAll = true,
                        onViewAllClick = { /* TODO */ },
                        onSurface = onSurface,
                        primary = primary
                    )
                }

                item {
                    LaunchedEffect(restaurants) {
                        Log.d("HomeScreen", "📊 Restaurantes a mostrar: ${restaurants.size}")
                    }

                    if (restaurants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay restaurantes disponibles",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(restaurants) { restaurant ->
                                AnimatedRestaurantCard(
                                    name = restaurant.name,
                                    rating = restaurant.rating,
                                    time = restaurant.deliveryTime,
                                    imageUrl = restaurant.imageUrl,
                                    imageRes = restaurant.imageRes,
                                    category = restaurant.category,
                                    onClick = {
                                        navController.navigate(Screen.RestaurantDetail.passId(restaurant.id))
                                    },
                                    surface = surface,
                                    onSurface = onSurface,
                                    onSurfaceVariant = onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── COMPONENTES ANIMADOS ─────────────────────────────────────────

@Composable
fun AnimatedPromoBanner(
    onClick: () -> Unit,
    primary: Color
) {
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bannerScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isHovered = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primary, primary.copy(alpha = 0.7f))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🔥 OFERTA ESPECIAL",
                        color = Color(0xFFFFD600),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¡Hasta 50% OFF!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "En tu primer pedido",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFD600),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primary: Color,
    onSurface: Color,
    surface: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipScale"
    )

    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isSelected) primary else surface,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun AnimatedSectionHeader(
    title: String,
    showViewAll: Boolean = false,
    onViewAllClick: () -> Unit = {},
    onSurface: Color,
    primary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
            if (showViewAll) {
                Text(
                    text = stringResource(id = R.string.label_view_all),
                    color = primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }
        }
    }
}

@Composable
fun AnimatedPromoFoodCard(
    name: String,
    price: String,
    oldPrice: String,
    imageUrl: String? = null,
    imageRes: Int = R.drawable.cheese,
    discount: Int = 0,
    onNavigate: () -> Unit,
    surface: Color,
    onSurface: Color,
    primary: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(0) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isPressed = true
                onNavigate()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen con badge de descuento
            Box {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop,
                        error = painterResource(imageRes)
                    )
                } else {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (discount > 0) {
                    Surface(
                        color = Color(0xFFE53935),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-$discount%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFFFF9800),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "🔥 OFERTA",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Info del producto
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = name,
                    fontSize = 12.sp,
                    color = onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = price,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = oldPrice,
                            color = onSurface.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }

                    // Botones + y -
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (quantity > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEEEEEE),
                                modifier = Modifier.size(28.dp),
                                onClick = { quantity-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "−",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }

                        if (quantity > 0) {
                            Text(
                                text = quantity.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurface,
                                modifier = Modifier.width(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = primary,
                            modifier = Modifier.size(28.dp),
                            onClick = { quantity++ }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "+",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedRestaurantCard(
    name: String,
    rating: String,
    time: String,
    imageUrl: String? = null,
    imageRes: Int = R.drawable.restaurantechino,
    category: String = "",
    onClick: () -> Unit,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "restCardScale"
    )

    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    Card(
        modifier = Modifier
            .width(220.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable { isPressed = true; onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop,
                        error = painterResource(imageRes)
                    )
                } else {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF1D9E75)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Abierto",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { isFavorite = !isFavorite },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite)
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFD32F2F)
                        else Color(0xFF888888),
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer { scaleX = heartScale; scaleY = heartScale }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Seleccionado por clientes",
                    fontSize = 11.sp,
                    color = onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (category.isNotEmpty()) category else "popular",
                        fontSize = 11.sp,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricChip(
                        modifier = Modifier.weight(1f),
                        icon = {
                            Icon(
                                Icons.Default.Star, null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        text = rating,
                        bgColor = Color(0xFFFFF8E1)
                    )

                    MetricChip(
                        modifier = Modifier.weight(1.4f),
                        icon = {
                            Icon(
                                Icons.Default.Schedule, null,
                                tint = Color(0xFF555555),
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        text = time,
                        bgColor = Color(0xFFF5F5F5)
                    )

                    MetricChip(
                        modifier = Modifier.weight(1.4f),
                        icon = {
                            Text("🛵", fontSize = 10.sp)
                        },
                        text = "\$3.500",
                        bgColor = Color(0xFFF5F5F5)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ver horario completo",
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { /* TODO: mostrar horario */ }
                )
            }
        }
    }
}

// ─── ANIMACIÓN DE ENTRADA (ESTRAMBÓTICA) ──────────────────────────

@Composable
fun FadeInEntrance(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100) // Pequeño retraso para que la UI se monte
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500, // Duración del fade (ajústalo a tu gusto)
            easing = FastOutSlowInEasing
        ),
        label = "fadeInAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                this.alpha = alpha
            }
    ) {
        content()
    }
}

// ─── METRIC CHIP ────────────────────────────────────────────────────

@Composable
private fun MetricChip(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String,
    bgColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
                maxLines = 1
            )
        }
    }
}