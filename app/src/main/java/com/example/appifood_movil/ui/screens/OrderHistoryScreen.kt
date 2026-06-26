// ui/screens/OrderHistoryScreen.kt
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.OrderHistoryViewModel
import kotlinx.coroutines.delay

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val SuccessGreen = Color(0xFF1D9E75)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
private val SurfaceGray  = Color(0xFFF7F7F7)

// ─────────────────────────────────────────────────────────────────
// OrderHistoryScreen
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController : NavController,
    viewModel     : OrderHistoryViewModel = hiltViewModel()
) {
    val orders    by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "historyFade"
    )
    val headerOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else (-30).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "headerSlide"
    )
    val contentOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 40.dp,
        animationSpec = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
        label         = "contentSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    // Filtros por estado
    val activeOrders = orders.filter { it.status == "pending" || it.status == "on_the_way" }
    val completedOrders = orders.filter { it.status == "delivered" || it.status == "cancelled" }
    val ordersToShow = if (selectedTabIndex == 0) activeOrders else completedOrders

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep))
            )
            .graphicsLayer { alpha = screenAlpha }
    ) {
        // Círculos decorativos — sistema de diseño unificado
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(260.dp).offset(x = (-80).dp, y = (-60).dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier.size(180.dp).align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = 40.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.04f))
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header sobre el gradiente ─────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = headerOffsetY)
                    .padding(top = 56.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón volver
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, "Volver",
                            tint = Color.White, modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ícono de historial
                Box(
                    modifier         = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Receipt, null,
                        tint     = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Mis Pedidos",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Línea acento amarilla
                Box(
                    modifier = Modifier
                        .width(40.dp).height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(YellowAccent)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tag pill con total de pedidos
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text       = "${orders.size} pedido${if (orders.size != 1) "s" else ""} registrado${if (orders.size != 1) "s" else ""}",
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }

            // ── Tarjeta blanca con tabs + lista ───────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = contentOffsetY)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // ── Tabs ──────────────────────────────────────
                    Surface(
                        color         = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor   = Color.White,
                            contentColor     = RedPrimary,
                            indicator        = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                        .height(3.dp),
                                    color    = RedPrimary
                                )
                            }
                        ) {
                            listOf(
                                "En curso" to Icons.Default.LocalShipping,
                                "Completados" to Icons.Default.CheckCircle
                            ).forEachIndexed { index, (title, icon) ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick  = { selectedTabIndex = index },
                                    text     = {
                                        Row(
                                            verticalAlignment     = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                icon, null,
                                                modifier = Modifier.size(16.dp),
                                                tint     = if (selectedTabIndex == index)
                                                    RedPrimary else TextMuted
                                            )
                                            Text(
                                                title,
                                                color      = if (selectedTabIndex == index)
                                                    RedPrimary else TextMuted,
                                                fontWeight = if (selectedTabIndex == index)
                                                    FontWeight.Bold else FontWeight.Normal,
                                                fontSize   = 13.sp
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // ── Contenido ─────────────────────────────────
                    when {
                        isLoading -> {
                            Box(
                                modifier         = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color       = RedPrimary,
                                        strokeWidth = 3.dp,
                                        modifier    = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Cargando pedidos...",
                                        color = TextMuted, fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        !error.isNullOrBlank() -> {
                            Box(
                                modifier         = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier            = Modifier.padding(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Warning, null,
                                        tint     = RedPrimary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Error al cargar pedidos",
                                        fontWeight = FontWeight.Bold,
                                        color      = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.loadOrders() },
                                        colors  = ButtonDefaults.buttonColors(
                                            containerColor = RedPrimary
                                        ),
                                        shape   = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Refresh, null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }

                        ordersToShow.isEmpty() -> {
                            OrderEmptyState(
                                isActive = selectedTabIndex == 0,
                                onExplore = {
                                    navController.navigate(Screen.Home.route)
                                }
                            )
                        }

                        else -> {
                            AnimatedContent(
                                targetState  = selectedTabIndex,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                                (slideOutHorizontally { -it } + fadeOut())
                                    } else {
                                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                                                (slideOutHorizontally { it } + fadeOut())
                                    }
                                },
                                label = "tabContent"
                            ) { _ ->
                                LazyColumn(
                                    contentPadding      = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier            = Modifier.fillMaxSize()
                                ) {
                                    itemsIndexed(
                                        items = ordersToShow,
                                        key   = { _, order -> order.orderId }
                                    ) { index, order ->
                                        // Entrada escalonada por índice
                                        AnimatedOrderCard(
                                            order       = order,
                                            index       = index,
                                            viewModel   = viewModel,
                                            onViewDetail = {
                                                navController.navigate(
                                                    Screen.OrderConfirmation.passId(order.orderId)
                                                )
                                            }
                                        )
                                    }

                                    item { Spacer(modifier = Modifier.height(24.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// AnimatedOrderCard — cada tarjeta entra escalonada
// ─────────────────────────────────────────────────────────────────
@Composable
fun AnimatedOrderCard(
    order        : Order,
    index        : Int,
    viewModel    : OrderHistoryViewModel,
    onViewDetail : () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 80L)   // entrada escalonada: cada card 80ms después
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInVertically(
            animationSpec  = tween(380, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 2 }
        )
    ) {
        OrderHistoryCard(
            order        = order,
            viewModel    = viewModel,
            onViewDetail = onViewDetail
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// OrderHistoryCard — tarjeta premium de pedido
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderHistoryCard(
    order        : Order,
    viewModel    : OrderHistoryViewModel,
    onViewDetail : () -> Unit
) {
    val statusColor = when (order.status) {
        "pending"    -> Color(0xFFFF9800)
        "on_the_way" -> Color(0xFF2196F3)
        "delivered"  -> SuccessGreen
        "cancelled"  -> Color(0xFFF44336)
        else         -> TextMuted
    }
    val statusLabel = when (order.status) {
        "pending"    -> "En preparación"
        "on_the_way" -> "En camino"
        "delivered"  -> "Entregado"
        "cancelled"  -> "Cancelado"
        else         -> order.status
    }
    val statusIcon = when (order.status) {
        "pending"    -> Icons.Default.HourglassEmpty
        "on_the_way" -> Icons.Default.LocalShipping
        "delivered"  -> Icons.Default.CheckCircle
        "cancelled"  -> Icons.Default.Cancel
        else         -> Icons.Default.Info
    }

    // Animación de presión
    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue   = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
            .clickable {
                isPressed = true
                onViewDetail()
            },
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Banda de estado en la parte superior ──────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(statusColor, statusColor.copy(alpha = 0.4f))
                        )
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // ── Fila superior: número de pedido + estado ──────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "#${order.orderId}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 15.sp,
                            color      = TextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(
                                horizontal = 10.dp, vertical = 5.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                statusIcon, null,
                                tint     = statusColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                statusLabel,
                                color      = statusColor,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Restaurante ───────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RedPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Restaurant, null,
                            tint     = RedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            order.restaurant.name.ifBlank { "Restaurante AppiFood" },
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = TextPrimary
                        )
                        Text(
                            viewModel.formatDate(order.timestamp),
                            fontSize = 12.sp,
                            color    = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // ── Productos (máximo 3 + "y N más") ─────────────
                val visibleItems = order.items.take(3)
                val remainingCount = order.items.size - visibleItems.size

                visibleItems.forEach { item ->
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "× ${item.quantity}  ${item.name}",
                            fontSize = 13.sp,
                            color    = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            viewModel.formatCurrency(item.subtotal),
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary
                        )
                    }
                }

                if (remainingCount > 0) {
                    Text(
                        "+$remainingCount producto${if (remainingCount > 1) "s" else ""} más",
                        fontSize = 12.sp,
                        color    = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // ── Fila inferior: método de pago + total + botón ─
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Método de pago
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            getPaymentIcon(order.payment.method),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            order.payment.method.ifBlank { "Efectivo" },
                            fontSize = 12.sp,
                            color    = TextMuted
                        )
                    }

                    // Total
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Total",
                            fontSize = 11.sp,
                            color    = TextMuted
                        )
                        Text(
                            viewModel.formatCurrency(order.total),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 16.sp,
                            color      = RedPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Botón ver detalle ─────────────────────────────
                OutlinedButton(
                    onClick  = onViewDetail,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = BorderStroke(1.5.dp, RedPrimary),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = RedPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.Receipt, null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ver comprobante",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// OrderEmptyState — estado vacío animado
// ─────────────────────────────────────────────────────────────────
@Composable
private fun OrderEmptyState(isActive: Boolean, onExplore: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyPulse")
    val bounce by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -12f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "emptyBounce"
    )

    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isActive) "🛵" else "📋",
                fontSize = 72.sp,
                modifier = Modifier.offset(y = bounce.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                if (isActive) "No tienes pedidos activos"
                else "No tienes pedidos completados",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 18.sp,
                color      = TextPrimary,
                textAlign  = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isActive) "¡Haz tu primer pedido y disfruta la mejor comida de Popayán!"
                else "Tus pedidos completados aparecerán aquí.",
                color     = TextMuted,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center
            )
            if (isActive) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick   = onExplore,
                    colors    = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape     = RoundedCornerShape(50),
                    modifier  = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explorar restaurantes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Helper: ícono emoji según método de pago ─────────────────────
private fun getPaymentIcon(method: String): String = when (method.lowercase()) {
    "nequi"      -> "📱"
    "bancolombia"-> "🏛️"
    "pse"        -> "💳"
    "daviplata"  -> "📲"
    "efectivo"   -> "💵"
    else         -> "💰"
}