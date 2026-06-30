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

// ── Paleta rol restaurante ────────────────────────────────────────
private val BluePrimary   = Color(0xFF1565C0)
private val BlueDark      = Color(0xFF0D47A1)
private val BlueDeep      = Color(0xFF002171)
private val BlueAccent    = Color(0xFF42A5F5)
private val YellowAccent  = Color(0xFFFFD600)
private val GreenSuccess  = Color(0xFF1D9E75)
private val OrangeWarn    = Color(0xFFF57F17)
private val RedAlert      = Color(0xFFD32F2F)
private val SurfaceLight  = Color(0xFFF0F4FF)
private val TextPrimary   = Color(0xFF1A1A1A)
private val TextMuted     = Color(0xFF888888)

@Composable
fun RestaurantDashboardScreen(
    navController     : NavController,
    authViewModel     : RestaurantAuthViewModel        = hiltViewModel(),
    dashboardViewModel: RestaurantDashboardViewModel   = hiltViewModel()
) {
    val user           = FirebaseAuth.getInstance().currentUser
    val restauranteId  = user?.uid ?: ""
    val platosActivos  by dashboardViewModel.platosActivos.collectAsState()

    LaunchedEffect(restauranteId) {
        if (restauranteId.isNotEmpty()) dashboardViewModel.loadPlatosActivos(restauranteId)
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
            // ── Header con gradiente azul ─────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = headerOffset)
                        .background(
                            Brush.verticalGradient(
                                listOf(BluePrimary, BlueDark, BlueDeep)
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

                        // Tag pill
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "🔵 Panel activo",
                                color      = Color.White,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // ── Stats en tarjetas con entrada escalonada ──────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(SurfaceLight)
                        .padding(top = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        "Resumen de hoy",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 17.sp,
                        color      = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Fila 1 de stats
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji    = "📦",
                            value    = "0",
                            label    = "Pedidos hoy",
                            color    = BluePrimary,
                            index    = 0
                        )
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji    = "💰",
                            value    = "\$0",
                            label    = "Ingresos",
                            color    = GreenSuccess,
                            index    = 1
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
                            emoji    = "⭐",
                            value    = "N/A",
                            label    = "Calificación",
                            color    = OrangeWarn,
                            index    = 2
                        )
                        AnimatedStatCard(
                            modifier = Modifier.weight(1f),
                            emoji    = "🍽️",
                            value    = platosActivos.toString(),
                            label    = "Platos activos",
                            color    = RedAlert,
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
                                .background(BluePrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Gestionar",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 17.sp,
                            color      = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // ── Cards de gestión con animación escalonada ─────────
            val gestionItems = listOf(
                GestionItem(
                    icon        = Icons.Default.Store,
                    emoji       = "🏪",
                    title       = "Información del restaurante",
                    description = "Nombre, descripción, dirección, teléfono y horarios",
                    color       = BluePrimary,
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
                    icon        = Icons.Default.Star,
                    emoji       = "⭐",
                    title       = "Reseñas",
                    description = "Lee y responde las reseñas de tus clientes",
                    color       = OrangeWarn,
                    route       = "gestionResenas"
                )
            )

            items(gestionItems.size) { index ->
                AnimatedGestionCard(
                    item          = gestionItems[index],
                    index         = index,
                    onNavigate    = { navController.navigate(gestionItems[index].route) }
                )
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
                // Ícono con gradiente
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

                // Flecha con color
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

// ── Stub de StatCard para evitar errores (ya no se usa pero puede quedar) ─
@Composable
fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {}

// ── Stub ManagementCard ───────────────────────────────────────────
@Composable
fun ManagementCard(title: String, description: String, icon: String = "📋", onClick: () -> Unit) {}