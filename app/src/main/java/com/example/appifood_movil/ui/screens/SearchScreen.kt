@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.SearchCriteria
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import android.location.Location
import kotlinx.coroutines.delay

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
private val SurfaceGray  = Color(0xFFF7F7F7)

// ─────────────────────────────────────────────────────────────────
// SearchScreen
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel     : SearchViewModel,
    navController : NavController
) {
    val criteria by viewModel.criteria.collectAsState()
    val results  by viewModel.filteredRestaurants.collectAsState(initial = emptyList())

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label         = "searchFadeIn"
    )
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        modifier       = Modifier.graphicsLayer { alpha = screenAlpha },
        containerColor = Color(0xFFF5F5F5),
        topBar         = {
            SearchTopBar(onBack = { navController.popBackStack() })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier        = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding  = PaddingValues(bottom = 32.dp)
        ) {
            // ── Campo de búsqueda ──────────────────────────────────
            item {
                SearchField(
                    query         = criteria.query,
                    onQueryChange = { viewModel.updateQuery(it) }
                )
            }

            // ── Header de filtros ──────────────────────────────────
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(RedPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Filtros de búsqueda",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 18.sp,
                        color      = TextPrimary
                    )
                }
            }

            // ── Slider de distancia — rango realista ──────────────
            item {
                FilterSliderCard(
                    title      = "📍 Distancia máxima",
                    // ── FIX: rango realista para Popayán ──────────
                    // Antes: 1..500 km (sin sentido para una ciudad)
                    // Ahora: 0.5..10 km — cubre toda la ciudad y
                    // sus alrededores inmediatos sin mostrar ciudades
                    // a 400km de distancia.
                    valueLabel = if (criteria.radiusKm >= 10.0)
                        "Toda la ciudad"
                    else
                        "${String.format("%.1f", criteria.radiusKm)} km",
                    value      = criteria.radiusKm.toFloat(),
                    onValueChange = { viewModel.updateRadius(it.toDouble()) },
                    valueRange = 0.5f..10f,
                    steps      = 18,   // pasos de ~0.5 km
                    minLabel   = "500 m",
                    maxLabel   = "10 km"
                )
            }

            // ── Slider de precio máximo ────────────────────────────
            item {
                FilterSliderCard(
                    title         = "💰 Precio máximo por plato",
                    valueLabel    = "\$${String.format("%,.0f", criteria.maxPrice).replace(",", ".")}",
                    value         = criteria.maxPrice.toFloat(),
                    onValueChange = { viewModel.updateMaxPrice(it.toDouble()) },
                    valueRange    = 5_000f..100_000f,
                    steps         = 18,
                    minLabel      = "\$5.000",
                    maxLabel      = "\$100.000"
                )
            }

            // ── Header de resultados ───────────────────────────────
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp).height(20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(RedPrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Resultados",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 18.sp,
                            color      = TextPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = RedPrimary
                    ) {
                        Text(
                            "${results.size}",
                            color      = Color.White,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Lista de resultados o estado vacío ─────────────────
            if (results.isEmpty()) {
                item { SearchEmptyState() }
            } else {
                itemsIndexed(
                    items = results,
                    key   = { _, r -> r.id }
                ) { index, restaurant ->
                    AnimatedSearchResultCard(
                        restaurant    = restaurant,
                        criteria      = criteria,
                        index         = index,
                        // ── FIX: usar passId() en lugar de interpolación ──
                        // Antes: "${Screen.RestaurantDetail.route}/${restaurant.id}"
                        // generaba "restaurantDetail/{id}/1" → crash
                        // Ahora: passId() genera "restaurantDetail/1" → correcto
                        onCardClick   = {
                            navController.navigate(
                                Screen.RestaurantDetail.passId(restaurant.id)
                            )
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// SearchTopBar
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Buscar restaurantes",
                fontWeight = FontWeight.ExtraBold,
                color      = TextPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// ─────────────────────────────────────────────────────────────────
// SearchField
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue   = if (isFocused) RedPrimary else Color(0xFFE0E0E0),
        animationSpec = tween(200),
        label         = "searchBorder"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().padding(top = 8.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border    = BorderStroke(1.5.dp, borderColor)
    ) {
        TextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            placeholder   = { Text("¿Qué se te antoja hoy? 🍔", color = TextMuted) },
            leadingIcon   = {
                Icon(
                    Icons.Default.Search, null,
                    tint = if (isFocused) RedPrimary else TextMuted
                )
            },
            trailingIcon  = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, null, tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            colors     = TextFieldDefaults.colors(
                focusedContainerColor    = Color.Transparent,
                unfocusedContainerColor  = Color.Transparent,
                focusedIndicatorColor    = Color.Transparent,
                unfocusedIndicatorColor  = Color.Transparent
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// FilterSliderCard — slider con etiquetas min/max y diseño premium
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSliderCard(
    title         : String,
    valueLabel    : String,
    value         : Float,
    onValueChange : (Float) -> Unit,
    valueRange    : ClosedFloatingPointRange<Float>,
    steps         : Int    = 0,
    minLabel      : String = "",
    maxLabel      : String = ""
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            // Título + valor actual
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = RedPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        valueLabel,
                        color      = RedPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 13.sp,
                        modifier   = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Slider
            Slider(
                value         = value,
                onValueChange = onValueChange,
                valueRange    = valueRange,
                steps         = steps,
                colors        = SliderDefaults.colors(
                    thumbColor        = RedPrimary,
                    activeTrackColor  = RedPrimary,
                    inactiveTrackColor = RedPrimary.copy(alpha = 0.2f)
                )
            )

            // Etiquetas min/max
            if (minLabel.isNotEmpty() || maxLabel.isNotEmpty()) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(minLabel, fontSize = 11.sp, color = TextMuted)
                    Text(maxLabel, fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// AnimatedSearchResultCard — tarjeta de resultado con entrada animada
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AnimatedSearchResultCard(
    restaurant  : Restaurant,
    criteria    : SearchCriteria,
    index       : Int,
    onCardClick : () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInHorizontally(
            animationSpec  = tween(350, easing = FastOutSlowInEasing),
            initialOffsetX = { it / 3 }
        )
    ) {
        SearchResultCard(
            restaurant  = restaurant,
            criteria    = criteria,
            onCardClick = onCardClick
        )
    }
}

@Composable
private fun SearchResultCard(
    restaurant  : Restaurant,
    criteria    : SearchCriteria,
    onCardClick : () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "resultCardScale"
    )

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable {
                isPressed = true
                onCardClick()
            },
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter            = painterResource(id = restaurant.imageRes),
                    contentDescription = restaurant.name,
                    modifier           = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale       = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Rating + categoría
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star, null,
                        tint     = Color(0xFFFFB300),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "${restaurant.rating} · ${restaurant.category}",
                        color    = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Distancia
                val distKm = calculateDistance(criteria.userLocation, restaurant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn, null,
                        tint     = RedPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        if (distKm < 1f)
                            "${(distKm * 1000).toInt()} m"
                        else
                            "${String.format("%.1f", distKm)} km",
                        color      = RedPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Tiempo estimado aproximado (1 km ≈ 3 min en moto)
                    val estMin = (distKm * 3 + 10).toInt()
                    Text(
                        "· ~$estMin min",
                        color    = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                Icons.Default.KeyboardArrowRight, null,
                tint = RedPrimary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// SearchEmptyState
// ─────────────────────────────────────────────────────────────────
@Composable
private fun SearchEmptyState() {
    val infiniteTransition = rememberInfiniteTransition(label = "emptySearch")
    val bounce by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "searchBounce"
    )

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🔍",
            fontSize = 64.sp,
            modifier = Modifier.offset(y = bounce.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Sin resultados",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 18.sp,
            color      = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Intenta ampliar el radio de distancia\no cambiar el término de búsqueda",
            color     = TextMuted,
            fontSize  = 14.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = 32.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Cálculo de distancia
// ─────────────────────────────────────────────────────────────────
fun calculateDistance(userLocation: Location?, restaurant: Restaurant): Float {
    if (userLocation == null) return 0f
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        userLocation.latitude,
        userLocation.longitude,
        restaurant.latitude,
        restaurant.longitude,
        results
    )
    return results[0] / 1000f
}