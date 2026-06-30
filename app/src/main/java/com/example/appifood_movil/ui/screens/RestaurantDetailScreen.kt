@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.example.appifood_movil.ui.map.MapStyles
import com.example.appifood_movil.ui.map.rememberMapViewWithLifecycle
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
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.maps.Style
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.components.AppiFoodFooter
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.appifood_movil.ui.viewmodel.RestaurantDetailViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.graphicsLayer
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyRow
import com.example.appifood_movil.navigation.Screen
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    navController: NavController,
    id: Int,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val platos by viewModel.platos.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    // ── LOG PARA VERIFICAR EL ID ─────────────────────────────────
    android.util.Log.d("RestaurantDetail", "ID recibido: $id")

    // ── CARGAR DATOS ──────────────────────────────────────────────
    LaunchedEffect(id) {
        android.util.Log.d("RestaurantDetail", "Cargando restaurante con ID: $id")
        viewModel.loadRestaurantDetail(id)
    }

    var isFavorite by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(1) }

    // ── ANIMACIÓN DE ENTRADA ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "detailFadeIn"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = RedPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando restaurante...",
                    color = TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    // ── VERIFICAR QUE EL RESTAURANTE EXISTA ──────────────────────
    if (restaurant == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Restaurante no encontrado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "El restaurante que buscas no existe",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Volver")
                }
            }
        }
        return
    }

    val restaurantData = restaurant!!
    val hasValidLocation = restaurantData.latitude != 0.0 && restaurantData.longitude != 0.0

    Scaffold(
        modifier = Modifier.graphicsLayer { alpha = screenAlpha },
        bottomBar = {
            AppiFoodFooter(navController, "home", 6, {})
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // ── HEADER CON IMAGEN ──────────────────────────────────
            item {
                RestaurantImageHeader(
                    imageUrl = restaurantData.imageUrl,
                    imageRes = restaurantData.imageRes,
                    name = restaurantData.name,
                    isFavorite = isFavorite,
                    onBack = { navController.popBackStack() },
                    onFavoriteToggle = { isFavorite = !isFavorite }
                )
            }

            // ── CONTENIDO PRINCIPAL ────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-28).dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 12.dp)
                ) {
                    // ── BADGE DE OFERTA ──────────────────────────────
                    AnimatedOfferBadge()

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── NOMBRE ─────────────────────────────────────────
                    Text(
                        text = restaurantData.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // ── RATING Y CATEGORÍA ────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = YellowAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = restaurantData.rating,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            val reviewCount = viewModel.reviews.collectAsState().value.size
                            Text(
                                text = " ($reviewCount reseñas)",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = RedPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = restaurantData.category,
                                color = RedPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // ── DIRECCIÓN ──────────────────────────────────────
                    if (restaurantData.address.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = restaurantData.address,
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("writeReview/${restaurantData.uid}/${restaurantData.name}")

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600),
                                    contentColor = Color.Black
                        ),

                    ) {
                        Icon(Icons.Default.Star, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escribir reseña")
                    }

                    // ── TABS ───────────────────────────────────────────
                    AnimatedTabs(
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it }
                    )
                }
            }

            // ── CONTENIDO DE LAS TABS ──────────────────────────────
            // ── CONTENIDO DE LAS TABS ──────────────────────────────
            when (selectedTabIndex) {
                0 -> { // ── FOTOS ──────────────────────────────────────
                    item {
                        AnimatedPhotosTab(photos = restaurantData.fotosGaleria)
                    }
                }

                1 -> { // ── MENÚ ────────────────────────────────────────
                    if (platos.isEmpty()) {
                        item {
                            EmptyStateMessage(
                                icon = Icons.Default.Restaurant,
                                message = "No hay platos disponibles"
                            )
                        }
                    } else {
                        items(platos) { plato ->
                            // ✅ Generar el ID de la misma manera que en getProducts()
                            val productId = restaurantData.id * 100 + platos.indexOf(plato)

                            AnimatedDishItem(
                                platoId = productId,  // ✅ Usar el mismo ID que en getProducts()
                                nombre = plato.nombre,
                                descripcion = plato.descripcion.ifEmpty { "Delicioso plato" },
                                precio = plato.precio,
                                precioPromocion = plato.precioPromocion,
                                descuento = plato.descuento,
                                imagenUrl = plato.imagenUrl,
                                onNavigate = {
                                    navController.navigate(Screen.ProductDetail.passId(productId))
                                }
                            )
                        }
                    }

                    item {
                        AnimatedMapSection(
                            hasValidLocation = hasValidLocation,
                            latitude = restaurantData.latitude,
                            longitude = restaurantData.longitude,
                            name = restaurantData.name,
                            address = restaurantData.address
                        )
                    }
                }

                2 -> { // ── RESEÑAS ────────────────────────────────────
                    if (reviews.isEmpty()) {
                        item {
                            EmptyStateMessage(
                                icon = Icons.Default.Comment,
                                message = "No hay reseñas aún"
                            )
                        }
                    } else {
                        items(reviews) { review ->   // ✅ Usar la lista actualizada
                            AnimatedReviewItem(review = review)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// ── HEADER CON IMAGEN ─────────────────────────────────────────────
@Composable
fun RestaurantImageHeader(
    imageUrl: String?,
    imageRes: Int,
    name: String,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    var imageScale by remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = imageScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "imageScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        AsyncImage(
            model = imageUrl ?: imageRes,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                },
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.restaurantechino)
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 0.4f,
                        endY = 1f
                    )
                )
        )

        // Círculos decorativos
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        // ── BOTÓN VOLVER ────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 54.dp, start = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    imageScale = 0.9f
                    onBack()
                },
            color = Color.White.copy(alpha = 0.3f),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── BOTÓN FAVORITO ──────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 54.dp, end = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    imageScale = 0.9f
                    onFavoriteToggle()
                },
            color = Color.White.copy(alpha = 0.3f),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) RedPrimary else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── NOMBRE EN LA IMAGEN ────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = YellowAccent,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🍽️ Destacado",
                    color = RedPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Text(
                text = name,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ── BADGE DE OFERTA ANIMADA ──────────────────────────────────────
@Composable
fun AnimatedOfferBadge() {
    var pulse by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pulse) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgePulse"
    )

    LaunchedEffect(Unit) {
        pulse = true
    }

    Surface(
        color = RedPrimary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "10% OFF en tu primer pedido",
                color = RedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

// ── TABS ANIMADOS ──────────────────────────────────────────────────
@Composable
fun AnimatedTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        " Fotos" to Icons.Default.Photo,
        " Menú" to Icons.Default.Restaurant,
        " Reseñas" to Icons.Default.Comment
    )

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = RedPrimary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp),
                color = RedPrimary
            )
        }
    ) {
        tabs.forEachIndexed { index, (title, icon) ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (index == selectedTabIndex) RedPrimary else TextMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            title,
                            color = if (index == selectedTabIndex) RedPrimary else TextMuted,
                            fontWeight = if (index == selectedTabIndex) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }
    }
}

// ── TABS DE FOTOS ──────────────────────────────────────────────────
@Composable
fun AnimatedPhotosTab(photos: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Galería de imágenes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Ver todas →",
                    color = RedPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay fotos en la galería",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos) { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto galería",
                            modifier = Modifier
                                .size(120.dp, 80.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.restaurantechino)
                        )
                    }
                }
            }
        }
    }
}
// ── PLATO ANIMADO ─────────────────────────────────────────────────
@Composable
fun AnimatedDishItem(
    platoId: Int,  // ✅ NUEVO: ID del plato
    nombre: String,
    descripcion: String,
    precio: Double,
    precioPromocion: Double = 0.0,
    descuento: Int = 0,
    imagenUrl: String? = null,
    onNavigate: () -> Unit  // ✅ NUEVO: Callback para navegar
) {
    var isPressed by remember { mutableStateOf(false) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dishScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isPressed = true
                onNavigate()  // ✅ Navegar al detail
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del plato
            if (!imagenUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imagenUrl,
                    contentDescription = nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.burguer)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.burguer),
                    contentDescription = nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    if (precioPromocion > 0 && descuento > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE53935).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "-$descuento% OFF",
                                color = Color(0xFFE53935),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = descripcion,
                    color = TextMuted,
                    fontSize = 13.sp,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (precioPromocion > 0) {
                        Text(
                            text = formatter.format(precioPromocion),
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = formatter.format(precio),
                            color = TextMuted,
                            fontSize = 13.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    } else {
                        Text(
                            text = formatter.format(precio),
                            color = RedPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = RedPrimary.copy(alpha = 0.1f),
                modifier = Modifier.clickable { onNavigate() }  // ✅ También navega al hacer clic en "Agregar"
            ) {
                Text(
                    text = "Agregar",
                    color = RedPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── MAPA ANIMADO ──────────────────────────────────────────────────
@Composable
fun AnimatedMapSection(
    hasValidLocation: Boolean,
    latitude: Double,
    longitude: Double,
    name: String,
    address: String
) {
    var mapVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mapVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .graphicsLayer {
                alpha = if (mapVisible) 1f else 0f
                translationY = if (mapVisible) 0f else 50f
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📍 Ubicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dirección
            if (address.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = address,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            // ── MAPA CON MAPLIBRE (100% GRATUITO) ──────────────────
            if (hasValidLocation) {
                val location = LatLng(latitude, longitude)
                val cameraPosition = CameraPosition.Builder()
                    .target(location)
                    .zoom(15.0)
                    .build()

                val mapView = rememberMapViewWithLifecycle()

                AndroidView(
                    factory = { mapView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) { view ->
                    view.getMapAsync { mapboxMap ->
                        mapboxMap.setStyle(MapStyles.openFreeMapStyleUrl) { style ->
                            mapboxMap.cameraPosition = cameraPosition
                            mapboxMap.markers.forEach { mapboxMap.removeMarker(it) } // evita duplicar markers en recomposición
                            mapboxMap.addMarker(
                                MarkerOptions().position(location).title(name).snippet(address)
                            )
                        }
                    }
                }
            } else {
                // ── MENSAJE SI NO HAY UBICACIÓN ──────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocationOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ubicación no disponible",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ── RESEÑA ANIMADA ────────────────────────────────────────────────
@Composable
fun AnimatedReviewItem(review: com.example.appifood_movil.data.model.Review) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RedPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(1).uppercase(),  // ✅ Cambiado
                            color = RedPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = review.userName,  // ✅ Cambiado
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        // Estrellas
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < review.rating) YellowAccent else Color(0xFFE0E0E0),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = formatDate(review.createdAt),  // ✅ Usar función de formato
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comentario
            Text(
                text = review.comment,
                color = TextPrimary,
                fontSize = 14.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                lineHeight = 20.sp
            )

            if (review.comment.length > 80) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (expanded) "Ver menos" else "Ver más",
                        color = RedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ✅ Agrega esta función de formato si no existe
private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}

// ── ESTADO VACÍO ──────────────────────────────────────────────────
@Composable
fun EmptyStateMessage(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = TextMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}