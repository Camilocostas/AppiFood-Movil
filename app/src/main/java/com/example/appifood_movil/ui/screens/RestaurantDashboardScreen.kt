// ui/screens/RestaurantDashboardScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

private val BluePrimary  = Color(0xFF1565C0)
private val BlueDark     = Color(0xFF0D47A1)
private val BlueDeep     = Color(0xFF002171)
private val YellowAccent = Color(0xFFFFD600)
private val RedPrimary   = Color(0xFFD32F2F)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
private val SurfaceGray  = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(navController: NavController) {

    val auth      = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Cargar datos básicos del restaurante
    var restaurantName by remember { mutableStateOf("Mi Restaurante") }
    var ownerName      by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("restaurants").document(uid).get()
            .addOnSuccessListener { doc ->
                restaurantName = doc.getString("restaurantName") ?: "Mi Restaurante"
                ownerName      = doc.getString("ownerName") ?: ""
            }
    }

    // Animación de entrada
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
            .background(Color(0xFFF5F5F5))
            .graphicsLayer { alpha = screenAlpha }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header azul con gradiente ─────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = headerOffset)
                    .background(
                        Brush.verticalGradient(listOf(BluePrimary, BlueDark))
                    )
                    .padding(top = 56.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
            ) {
                // Círculos decorativos
                Box(modifier = Modifier.size(150.dp).offset(x = 200.dp, y = (-40).dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.05f)))

                Column {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "¡Hola, $ownerName! 👋",
                                color    = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                            Text(
                                restaurantName,
                                color      = Color.White,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        // Avatar
                        Box(
                            modifier         = Modifier.size(48.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏪", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tag pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            "Panel de administración",
                            color      = Color.White,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // ── Grid de opciones ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Estadísticas rápidas
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        emoji    = "📦",
                        label    = "Pedidos hoy",
                        value    = "0",
                        color    = BluePrimary,
                        index    = 0
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        emoji    = "💰",
                        label    = "Ingresos hoy",
                        value    = "\$0",
                        color    = Color(0xFF2E7D32),
                        index    = 1
                    )
                }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        emoji    = "⭐",
                        label    = "Calificación",
                        value    = "N/A",
                        color    = Color(0xFFF57F17),
                        index    = 2
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        emoji    = "🍽️",
                        label    = "Platos activos",
                        value    = "0",
                        color    = Color(0xFF6A1B9A),
                        index    = 3
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Gestionar",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 17.sp,
                    color      = TextPrimary
                )

                // Opciones de gestión
                val options = listOf(
                    DashOption("Mis platos",         Icons.Default.Restaurant,     Color(0xFF1565C0), "Agrega, edita o elimina platos de tu menú"),
                    DashOption("Pedidos",            Icons.Default.ShoppingBag,    Color(0xFF2E7D32), "Gestiona los pedidos entrantes en tiempo real"),
                    DashOption("Fotos",              Icons.Default.PhotoCamera,    Color(0xFF6A1B9A), "Sube y organiza las fotos de tu restaurante"),
                    DashOption("Reseñas",            Icons.Default.Star,           Color(0xFFF57F17), "Lee y responde las reseñas de tus clientes"),
                    DashOption("Estadísticas",       Icons.Default.BarChart,       Color(0xFF00695C), "Analiza el rendimiento de tu restaurante"),
                    DashOption("Configuración",      Icons.Default.Settings,       TextMuted,         "Horarios, dirección, información de contacto")
                )

                options.forEachIndexed { index, option ->
                    DashOptionCard(option = option, index = index)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón cerrar sesión
                OutlinedButton(
                    onClick  = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.RoleSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    border   = BorderStroke(1.5.dp, RedPrimary),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary)
                ) {
                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

data class DashOption(
    val title       : String,
    val icon        : ImageVector,
    val color       : Color,
    val description : String
)

@Composable
private fun QuickStatCard(
    modifier : Modifier,
    emoji    : String,
    label    : String,
    value    : String,
    color    : Color,
    index    : Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 60L); visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInVertically(
            tween(350, easing = FastOutSlowInEasing) ) { it / 2 },
        modifier = modifier
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
                Text(label, fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun DashOptionCard(option: DashOption, index: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 50L); visible = true }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.97f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "dashCardScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInHorizontally(
            tween(350, easing = FastOutSlowInEasing)) { it / 3 }
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clickable { isPressed = true },
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                        .background(option.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(option.icon, null, tint = option.color,
                        modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(option.title, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = TextPrimary)
                    Text(option.description, fontSize = 12.sp, color = TextMuted)
                }
                Icon(Icons.Default.ChevronRight, null, tint = TextMuted,
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}