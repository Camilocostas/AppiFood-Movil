@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.example.appifood_movil.navigation.Screen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appifood_movil.data.model.Review
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.ui.viewmodel.RestaurantAuthViewModel
import com.example.appifood_movil.ui.viewmodel.RestaurantDashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

// ── Paleta AppiFood (ROJO) ────────────────────────────────────────
private val AppiRed     = Color(0xFFD32F2F)
private val AppiRedDark = Color(0xFFB71C1C)
private val AppiRedDeep = Color(0xFF7F0000)
private val AppiYellow  = Color(0xFFFFD600)
private val GreenSuccess = Color(0xFF1D9E75)
private val OrangeWarn  = Color(0xFFF57F17)
private val SurfaceLight = Color(0xFFF5F5F5)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@Composable
fun RestaurantDashboardScreen(
    navController     : NavController,
    authViewModel     : RestaurantAuthViewModel        = hiltViewModel(),
    dashboardViewModel: RestaurantDashboardViewModel   = hiltViewModel()
) {
    val user           = FirebaseAuth.getInstance().currentUser
    val restauranteId  = user?.uid ?: ""
    val platosActivos by dashboardViewModel.platosActivos.collectAsState()
    val pedidosHoy by dashboardViewModel.pedidosHoy.collectAsState()
    val ingresosHoy by dashboardViewModel.ingresosHoy.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val averageRating by dashboardViewModel.averageRating.collectAsState()
    val reviews by dashboardViewModel.reviews.collectAsState()

    val ratingDisplay = remember(averageRating) {
        if (averageRating > 0.0) String.format("%.1f", averageRating) else "N/A"
    }

    LaunchedEffect(restauranteId) {
        if (restauranteId.isNotEmpty()) {
            dashboardViewModel.loadDashboardData(restauranteId)
        }
    }

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "dashFade"
    )
    val headerOffset by animateDpAsState(
        targetValue   = if (visible) 0.dp else (-30).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "dashHeader"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .graphicsLayer { alpha = screenAlpha }
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Header con gradiente ROJO ─────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = headerOffset)
                        .background(
                            Brush.verticalGradient(
                                listOf(AppiRed, AppiRedDark, AppiRedDeep)
                            )
                        )
                        .padding(top = 52.dp, bottom = 32.dp,
                            start = 24.dp, end = 24.dp)
                ) {
                    // Círculos decorativos
                    Box(modifier = Modifier.size(200.dp)
                        .offset(x = 180.dp, y = (-50).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f)))
                    Box(modifier = Modifier.size(120.dp)
                        .offset(x = (-30).dp, y = 60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f)))

                    Column {
                        // TopBar dentro del header
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "Panel de Administración",
                                color      = Color.White.copy(alpha = 0.75f),
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            // Cerrar sesión
                            Box(
                                modifier         = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable {
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate("roleSelection") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Logout, "Cerrar sesión",
                                    tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Avatar + bienvenida
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier         = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏪", fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    "¡Hola, ${user?.displayName ?: "Restaurante"}! 👋",
                                    color      = Color.White,
                                    fontSize   = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    "Gestiona tu restaurante desde aquí",
                                    color    = Color.White.copy(alpha = 0.75f),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tag pill AMARILLO
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AppiYellow.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "● Panel activo",
                                    color      = Color.White,
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // ── Stats en tarjetas ──────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(SurfaceLight)
                        .padding(top = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📊 Resumen de hoy",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 18.sp,
                            color      = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AppiRed.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                                color = AppiRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Fila 1 de stats
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "📦",
                            value = pedidosHoy.toString(),
                            label = "Pedidos hoy",
                            color = AppiRed,
                            index = 0
                        )
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "💰",
                            value = "$${String.format("%,.0f", ingresosHoy)}",
                            label = "Ingresos",
                            color = GreenSuccess,
                            index = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fila 2 de stats
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji = "⭐",
                            value = ratingDisplay,
                            label = "Calificación",
                            color = OrangeWarn,
                            index = 2
                        )
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji    = "🍽️",
                            value    = platosActivos.toString(),
                            label    = "Platos activos",
                            color    = AppiRed,
                            index    = 3
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Sección gestionar ─────────────────────────
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp).height(20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AppiRed)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "⚡ Gestionar",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 17.sp,
                            color      = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // ── Cards de gestión ──────────────────────────────────
            val gestionItems = listOf(
                GestionItem(
                    icon        = Icons.Default.Store,
                    emoji       = "🏪",
                    title       = "Información del restaurante",
                    description = "Nombre, descripción, dirección, teléfono y horarios",
                    color       = AppiRed,
                    route       = "gestionInfoRestaurante"
                ),
                GestionItem(
                    icon        = Icons.Default.Restaurant,
                    emoji       = "🍽️",
                    title       = "Mis platos",
                    description = "Agrega, edita, elimina y gestiona promociones",
                    color       = GreenSuccess,
                    route       = "gestionPlatos"
                ),
                GestionItem(
                    icon        = Icons.Default.Receipt,
                    emoji       = "📋",
                    title       = "Pedidos",
                    description = "Visualiza y gestiona los pedidos entrantes",
                    color       = Color(0xFF1565C0),
                    route       = "restaurant_orders"
                )
            )

            items(gestionItems.size) { index ->
                AnimatedGestionCard(
                    item          = gestionItems[index],
                    index         = index,
                    onNavigate    = { navController.navigate(gestionItems[index].route) }
                )
            }

            // ── Reseñas ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AppiYellow)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "⭐ Reseñas de clientes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }
            }

            items(reviews.take(3)) { review ->
                ReviewCard(review = review)
            }

            if (reviews.size > 3) {
                item {
                    TextButton(
                        onClick = { navController.navigate("gestionResenas") },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            "Ver todas las reseñas (${reviews.size})",
                            color = AppiRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

data class GestionItem(
    val icon        : ImageVector,
    val emoji       : String,
    val title       : String,
    val description : String,
    val color       : Color,
    val route       : String
)

// ── Stat card animada ─────────────────────────────────────────────
@Composable
private fun AnimatedStatCard(
    modifier : Modifier,
    emoji    : String,
    value    : String,
    label    : String,
    color    : Color,
    index    : Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 80L); visible = true }

    AnimatedVisibility(
        visible  = visible,
        enter    = fadeIn(tween(300)) + slideInVertically(tween(380)) { it / 2 },
        modifier = modifier
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Column {
                        Text(
                            value,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 24.sp,
                            color      = color
                        )
                        Text(label, fontSize = 12.sp, color = TextMuted)
                    }
                    Box(
                        modifier         = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Barra de progreso decorativa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(color)
                    )
                }
            }
        }
    }
}

// ── Review Card ────────────────────────────────────────────────────
@Composable
fun ReviewCard(review: com.example.appifood_movil.data.model.Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
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
                    text = review.userName.ifBlank { "Usuario" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = if (index < review.rating) AppiYellow else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (review.comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = review.comment,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(review.createdAt),
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}

// ── Gestión card animada ──────────────────────────────────────────
@Composable
private fun AnimatedGestionCard(
    item       : GestionItem,
    index      : Int,
    onNavigate : () -> Unit
) {
    var visible   by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(index * 70L + 200L); visible = true }

    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.97f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "gestionScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInHorizontally(
            tween(380, easing = FastOutSlowInEasing)) { it / 3 },
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp)
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clickable { isPressed = true; onNavigate() },
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    item.color.copy(alpha = 0.2f),
                                    item.color.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 15.sp,
                        color      = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item.description,
                        fontSize = 12.sp,
                        color    = TextMuted,
                        maxLines = 2,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier         = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(item.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronRight, null,
                        tint     = item.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}