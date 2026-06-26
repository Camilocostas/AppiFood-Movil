package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.ui.viewmodel.HomeViewModel
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.components.CarouselHeader
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import coil.compose.AsyncImage
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.theme.FoodRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel
) {
    val filteredRestaurants by searchViewModel.filteredRestaurants.collectAsState()

    // ✅ Obtener colores del tema
    val colorScheme = MaterialTheme.colorScheme
    val background = colorScheme.background
    val surface = colorScheme.surface
    val onSurface = colorScheme.onSurface
    val onSurfaceVariant = colorScheme.onSurfaceVariant
    val primary = colorScheme.primary

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "homeFadeIn"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha },
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
                .background(background)  // ✅ Usa color del tema
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
                    primary = primary  // ✅ Pasamos el color del tema
                )
            }

            // ── CATEGORÍAS ─────────────────────────────────────────
            // ── CATEGORÍAS ─────────────────────────────────────────
            item {
                AnimatedSectionHeader(
                    title = stringResource(id = R.string.section_categories),
                    onSurface = onSurface,
                    primary = primary
                )
                val categories = listOf("Todos", "Bebidas", "Postres", "Rapida", "Oriental", "Mexicana", "Vegetariana")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = (cat == "Todos" && viewModel.selectedCategory == "Todas") || viewModel.selectedCategory == cat
                        AnimatedCategoryChip(
                            text = cat,
                            isSelected = isSelected,
                            onClick = { viewModel.onCategorySelected(cat) },
                            primary = primary,
                            onSurface = onSurface,
                            surface = surface  // ✅ AÑADE ESTA LÍNEA
                        )
                    }
                }
            }

            // ── PROMOCIONES ────────────────────────────────────────
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
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(viewModel.filteredProducts) { product ->
                        AnimatedPromoFoodCard(
                            name = product.name,
                            price = "$ ${String.format("%,.0f", product.price)}",
                            oldPrice = "$35.000",
                            imageRes = product.imageRes,
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
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRestaurants) { restaurant ->
                        AnimatedRestaurantCard(
                            name = restaurant.name,
                            rating = restaurant.rating,
                            time = stringResource(id = R.string.delivery_time_range),
                            imageUrl = restaurant.imageUrl,
                            imageRes = restaurant.imageRes,
                            onClick = { navController.navigate(Screen.RestaurantDetail.passId(restaurant.id)) },
                            surface = surface,
                            onSurface = onSurface,
                            onSurfaceVariant = onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// ── PROMO BANNER CON ANIMACIÓN ──────────────────────────────────
@Composable
fun AnimatedPromoBanner(
    onClick: () -> Unit,
    primary: Color  // ✅ Recibe el color del tema
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
                        colors = listOf(primary, primary.copy(alpha = 0.7f))  // ✅ Usa el color del tema
                    )
                )
        ) {
            // Decoración
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
                            tint = primary,  // ✅ Usa el color del tema
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── CATEGORY CHIP CON ANIMACIÓN ──────────────────────────────────
// ── CATEGORY CHIP CON ANIMACIÓN ──────────────────────────────────
@Composable
fun AnimatedCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primary: Color,
    onSurface: Color,
    surface: Color  // ✅ Añadimos el color de superficie
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
        color = if (isSelected) primary else surface,  // ✅ Cambiado: usa surface en lugar de Color.White
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else onSurface,  // ✅ Usa onSurface para texto
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

// ── SECTION HEADER CON ANIMACIÓN ─────────────────────────────────
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
                .background(primary)  // ✅ Usa color del tema
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
                color = onSurface  // ✅ Usa color del tema
            )
            if (showViewAll) {
                Text(
                    text = stringResource(id = R.string.label_view_all),
                    color = primary,  // ✅ Usa color del tema
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }
        }
    }
}

// ── PROMO FOOD CARD CON ANIMACIÓN ───────────────────────────────
@Composable
fun AnimatedPromoFoodCard(
    name: String,
    price: String,
    oldPrice: String,
    imageRes: Int,
    onNavigate: () -> Unit,
    surface: Color,
    onSurface: Color,
    primary: Color
) {
    var isPressed by remember { mutableStateOf(false) }

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
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = surface  // ✅ Usa color del tema
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = primary,  // ✅ Usa color del tema
                    shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_offer),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = Color(0xFFFFD600),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "%",
                            color = primary,  // ✅ Usa color del tema
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = onSurface  // ✅ Usa color del tema
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = price,
                        color = primary,  // ✅ Usa color del tema
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = oldPrice,
                        color = onSurface.copy(alpha = 0.5f),  // ✅ Usa color del tema
                        fontSize = 11.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    )
                }
            }
        }
    }
}

// ── RESTAURANT CARD CON ANIMACIÓN ───────────────────────────────
@Composable
fun AnimatedRestaurantCard(
    name: String,
    rating: String,
    time: String,
    imageUrl: String? = null,
    imageRes: Int,
    onClick: () -> Unit,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "restCardScale"
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = surface  // ✅ Usa color del tema
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                onSurface.copy(alpha = 0.05f),
                                onSurface.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl ?: imageRes,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.restaurantechino)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                color = onSurface  // ✅ Usa color del tema
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = FoodRating.copy(alpha = 0.15f),
                    modifier = Modifier.size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = FoodRating,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = rating,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface  // ✅ Usa color del tema
                )
                Text(
                    text = " • $time",
                    fontSize = 11.sp,
                    color = onSurfaceVariant  // ✅ Usa color del tema
                )
            }
        }
    }
}