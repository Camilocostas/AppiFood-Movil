// ui/screens/CartScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.ReceiptItem
import com.example.appifood_movil.data.model.RestaurantInfo
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.viewmodel.OrderViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// ── Paleta unificada AppiFood ─────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val SuccessGreen = Color(0xFF1D9E75)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
private val SurfaceGray  = Color(0xFFF7F7F7)

// ─────────────────────────────────────────────────────────────────
// CartScreen
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController  : NavController,
    viewModel      : CartViewModel  = hiltViewModel(),
    authViewModel  : AuthViewModel  = hiltViewModel(),
    orderViewModel : OrderViewModel = hiltViewModel()
) {
    val cartItems     = viewModel.cartItems
    val subtotal      = viewModel.subtotal
    val total         = viewModel.total
    val couponCode    by viewModel.couponCode
    val couponApplied by viewModel.couponApplied
    val couponError   by viewModel.couponError

    // ── Estados del sheet ─────────────────────────────────────────
    var showOrderForm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Animación de entrada ──────────────────────────────────────
    var screenVisible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (screenVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label         = "cartFade"
    )
    LaunchedEffect(Unit) { screenVisible = true }

    // ── Box raíz: contiene el Scaffold Y el BottomSheet ──────────
    // El BottomSheet DEBE estar fuera del Scaffold pero dentro
    // del mismo Box para que se superponga correctamente sobre
    // toda la pantalla, incluyendo la TopAppBar.
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                CartTopBar(
                    onBack    = { navController.popBackStack() },
                    onClear   = { viewModel.clearCart() },
                    itemCount = cartItems.size
                )
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .graphicsLayer { alpha = screenAlpha }
            ) {
                if (cartItems.isEmpty()) {
                    EmptyCartState(modifier = Modifier.weight(1f))
                } else {
                    // ── Lista de productos ────────────────────────
                    LazyColumn(
                        modifier            = Modifier.weight(1f),
                        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn(tween(300)) + slideInHorizontally(
                                    animationSpec  = tween(350, easing = FastOutSlowInEasing),
                                    initialOffsetX = { it / 3 }
                                )
                            ) {
                                CartItemCard(item = item, viewModel = viewModel)
                            }
                        }
                    }

                    // ── Sección inferior ──────────────────────────
                    CartBottomSection(
                        viewModel     = viewModel,
                        couponCode    = couponCode,
                        couponApplied = couponApplied,
                        couponError   = couponError,
                        subtotal      = subtotal,
                        shipping      = viewModel.shipping,
                        total         = total,
                        // ── Aquí se pasa la acción de abrir el sheet ──
                        onConfirmClick = { showOrderForm = true }
                    )
                }
            }
        }

        // ── BottomSheet fuera del Scaffold, dentro del Box ────────
        // Esto garantiza que se superpone correctamente sobre
        // toda la pantalla sin ser cortado por el padding del Scaffold.
        if (showOrderForm) {
            OrderFormBottomSheet(
                sheetState     = sheetState,
                cartViewModel  = viewModel,
                authViewModel  = authViewModel,
                orderViewModel = orderViewModel,
                restaurantInfo = RestaurantInfo(
                    // TODO: sustituir con el restaurante real cuando
                    // el carrito esté vinculado a un restaurante específico.
                    // Por ahora se usa un stub hasta conectar con el
                    // RestaurantDetailViewModel o un estado global.
                    id    = 1,
                    name  = "Restaurante AppiFood",
                    phone = "3001234567"
                ),
                onDismiss  = { showOrderForm = false },
                onOrderPlaced = { orderId ->
                    showOrderForm = false
                    navController.navigate(Screen.OrderConfirmation.passId(orderId))
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// CartTopBar — badge circular con size fijo
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(onBack: () -> Unit, onClear: () -> Unit, itemCount: Int) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Mi carrito", fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                if (itemCount > 0) {
                    Box(
                        modifier         = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(RedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = if (itemCount > 99) "99+" else "$itemCount",
                            color      = Color.White,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = TextPrimary)
            }
        },
        actions = {
            if (itemCount > 0) {
                TextButton(onClick = onClear) {
                    Text("Vaciar", color = RedPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// ─────────────────────────────────────────────────────────────────
// CartItemCard — controles horizontales
// ─────────────────────────────────────────────────────────────────
@Composable
fun CartItemCard(item: ReceiptItem, viewModel: CartViewModel) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier           = Modifier.size(54.dp),
                    contentScale       = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, color = TextPrimary, maxLines = 2)
                Spacer(modifier = Modifier.height(3.dp))
                Text(viewModel.formatCurrency(item.price),
                    fontSize = 13.sp, color = RedPrimary, fontWeight = FontWeight.SemiBold)
                Text("Subtotal: ${viewModel.formatCurrency(item.price * item.quantity)}",
                    fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.width(8.dp))

            HorizontalQuantityControl(
                quantity   = item.quantity,
                onIncrease = { viewModel.increaseQuantity(item) },
                onDecrease = { viewModel.decreaseQuantity(item) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// HorizontalQuantityControl — [ − ] número [ + ]
// ─────────────────────────────────────────────────────────────────
@Composable
fun HorizontalQuantityControl(
    quantity   : Int,
    onIncrease : () -> Unit,
    onDecrease : () -> Unit
) {
    var prevQty by remember { mutableIntStateOf(quantity) }
    var triggerBounce by remember { mutableStateOf(false) }
    val numScale by animateFloatAsState(
        targetValue      = if (triggerBounce) 1.4f else 1f,
        animationSpec    = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        finishedListener = { triggerBounce = false },
        label            = "qtyBounce"
    )
    LaunchedEffect(quantity) {
        if (quantity != prevQty) { prevQty = quantity; triggerBounce = true }
    }

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier         = Modifier.size(30.dp).clip(CircleShape)
                .background(if (quantity == 1) Color(0xFFFFEBEB) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector        = if (quantity == 1) Icons.Default.Delete
                    else Icons.Default.Remove,
                    contentDescription = "Disminuir",
                    tint               = if (quantity == 1) RedPrimary else TextMuted,
                    modifier           = Modifier.size(15.dp)
                )
            }
        }

        Text("$quantity", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
            color = TextPrimary, textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 22.dp).scale(numScale))

        Box(
            modifier         = Modifier.size(30.dp).clip(CircleShape).background(RedPrimary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp)) {
                Icon(Icons.Default.Add, "Aumentar", tint = Color.White,
                    modifier = Modifier.size(15.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// CartBottomSection — ahora recibe onConfirmClick como parámetro
// ─────────────────────────────────────────────────────────────────
@Composable
fun CartBottomSection(
    viewModel      : CartViewModel,
    couponCode     : String,
    couponApplied  : Boolean,
    couponError    : String?,
    subtotal       : Int,
    shipping       : Int,
    total          : Int,
    onConfirmClick : () -> Unit       // ← nuevo parámetro
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp, top = 8.dp)
    ) {
        CouponField(
            value         = couponCode,
            onValueChange = { viewModel.couponCode.value = it },
            onApply       = { viewModel.applyCoupon() },
            isApplied     = couponApplied,
            error         = couponError
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = SurfaceGray),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow("Subtotal",  viewModel.formatCurrency(subtotal))
                SummaryRow("Domicilio", viewModel.formatCurrency(shipping))
                if (couponApplied) {
                    SummaryRow("🎉 Cupón BIENVENIDO", "-\$0", isPromo = true)
                }
                HorizontalDivider(
                    color     = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier  = Modifier.padding(vertical = 10.dp)
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Text(viewModel.formatCurrency(total), fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold, color = RedPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Botón pasa onConfirmClick al hacer clic ───────────────
        ConfirmOrderButton(onConfirm = onConfirmClick)
    }
}

// ─────────────────────────────────────────────────────────────────
// ConfirmOrderButton — animación interna completa
// ─────────────────────────────────────────────────────────────────
private enum class BtnPhase { IDLE, LAUNCHED, RIDING, DONE }

@Composable
fun ConfirmOrderButton(onConfirm: () -> Unit) {

    var phase by remember { mutableStateOf(BtnPhase.IDLE) }

    data class Particle(val offsetX: Float, val color: Color, val size: Float)
    val confettiColors = listOf(YellowAccent, Color.White, Color(0xFFFF8F00),
        Color(0xFFFFEBEB), SuccessGreen)
    val particles = remember {
        List(14) {
            Particle(
                offsetX = Random.nextFloat() * 2f - 1f,
                color   = confettiColors[it % confettiColors.size],
                size    = Random.nextFloat() * 6f + 5f
            )
        }
    }

    val infiniteIdle = rememberInfiniteTransition(label = "idle")
    val idlePulse by infiniteIdle.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.025f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "idlePulse"
    )

    val btnColor by animateColorAsState(
        targetValue   = if (phase == BtnPhase.IDLE || phase == BtnPhase.DONE)
            RedPrimary else SuccessGreen,
        animationSpec = tween(400),
        label         = "btnColor"
    )

    val btnScale by animateFloatAsState(
        targetValue   = when (phase) {
            BtnPhase.LAUNCHED -> 1.06f
            BtnPhase.IDLE     -> idlePulse
            else              -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "btnScale"
    )

    val particleProgress by animateFloatAsState(
        targetValue   = if (phase == BtnPhase.LAUNCHED || phase == BtnPhase.RIDING) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label         = "confetti"
    )

    val motoOffset by animateFloatAsState(
        targetValue   = if (phase == BtnPhase.RIDING) 1f else -0.1f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label         = "motoRide"
    )

    val btnText = when (phase) {
        BtnPhase.IDLE     -> "Confirmar pedido"
        BtnPhase.LAUNCHED -> "¡Procesando!"
        BtnPhase.RIDING   -> "Abriendo formulario... 🛵"
        BtnPhase.DONE     -> "Confirmar pedido"
    }

    // ── La animación dura ~2s y luego abre el sheet ───────────────
    LaunchedEffect(phase) {
        when (phase) {
            BtnPhase.LAUNCHED -> {
                delay(600)
                phase = BtnPhase.RIDING
            }
            BtnPhase.RIDING -> {
                delay(900)
                // Al terminar la animación, dispara el callback
                // que abre el BottomSheet en CartScreen
                onConfirm()
                delay(400)
                phase = BtnPhase.DONE
            }
            BtnPhase.DONE -> {
                delay(300)
                phase = BtnPhase.IDLE
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(btnScale)
    ) {
        Button(
            onClick = {
                // Solo dispara la animación — el sheet se abre
                // al terminar la fase RIDING (ver LaunchedEffect)
                if (phase == BtnPhase.IDLE) {
                    phase = BtnPhase.LAUNCHED
                }
            },
            modifier  = Modifier.fillMaxSize(),
            shape     = RoundedCornerShape(18.dp),
            colors    = ButtonDefaults.buttonColors(
                containerColor = btnColor,
                contentColor   = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            AnimatedContent(
                targetState  = btnText,
                transitionSpec = {
                    (fadeIn(tween(250)) + slideInVertically { -it }) togetherWith
                            (fadeOut(tween(150)) + slideOutVertically { it })
                },
                label = "btnText"
            ) { text ->
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (phase == BtnPhase.IDLE) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // ── Confeti ───────────────────────────────────────────────
        if (phase == BtnPhase.LAUNCHED || phase == BtnPhase.RIDING) {
            particles.forEach { p ->
                val xFraction = (p.offsetX + 1f) / 2f
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(xFraction).fillMaxHeight()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(y = ((-28f) * particleProgress
                                        + 28f * particleProgress * particleProgress).dp)
                                .graphicsLayer {
                                    alpha     = 1f - particleProgress * 0.6f
                                    rotationZ = particleProgress * 360f *
                                            (if (p.offsetX > 0) 1 else -1)
                                    scaleX    = 1f - particleProgress * 0.3f
                                    scaleY    = 1f - particleProgress * 0.3f
                                }
                                .size(p.size.dp)
                                .clip(
                                    if (p.size > 9f) RoundedCornerShape(2.dp) else CircleShape
                                )
                                .background(p.color)
                        )
                    }
                }
            }
        }

        // ── Moto ──────────────────────────────────────────────────
        if (phase == BtnPhase.RIDING) {
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp))) {
                repeat(3) { i ->
                    Text(
                        text     = "🛵",
                        fontSize = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth(motoOffset.coerceIn(0f, 1f))
                            .wrapContentWidth(Alignment.End)
                            .align(Alignment.Center)
                            .offset(x = (-(i * 12)).dp)
                            .graphicsLayer { alpha = (0.15f - i * 0.04f).coerceAtLeast(0f) }
                    )
                }
                Text(
                    text     = "🛵",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth(motoOffset.coerceIn(0f, 1f))
                        .wrapContentWidth(Alignment.End)
                        .align(Alignment.Center)
                        .graphicsLayer {
                            translationY = sin(motoOffset * Math.PI.toFloat() * 4) * 3f
                        }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// CouponField
// ─────────────────────────────────────────────────────────────────
@Composable
fun CouponField(
    value         : String,
    onValueChange : (String) -> Unit,
    onApply       : () -> Unit,
    isApplied     : Boolean,
    error         : String?
) {
    Column {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            enabled       = !isApplied,
            placeholder   = { Text("Código de cupón (ej: BIENVENIDO)", fontSize = 13.sp) },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = RedPrimary,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            trailingIcon = {
                if (isApplied) {
                    Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen)
                } else {
                    TextButton(onClick = onApply) {
                        Text("Aplicar", color = RedPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            singleLine = true
        )
        AnimatedVisibility(visible = !error.isNullOrBlank()) {
            Text(error ?: "", color = RedPrimary, fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp))
        }
        AnimatedVisibility(visible = isApplied) {
            Text("✓ Cupón aplicado correctamente", color = SuccessGreen, fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// EmptyCartState
// ─────────────────────────────────────────────────────────────────
@Composable
fun EmptyCartState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty")
    val bounce by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "emptyBounce"
    )
    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🛒", fontSize = 72.sp, modifier = Modifier.offset(y = bounce.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tu carrito está vacío", fontSize = 18.sp,
            fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Agrega productos deliciosos\npara comenzar tu pedido",
            fontSize = 14.sp, color = TextMuted, textAlign = TextAlign.Center)
    }
}

// ─────────────────────────────────────────────────────────────────
// SummaryRow
// ─────────────────────────────────────────────────────────────────
@Composable
fun SummaryRow(
    label   : String,
    value   : String,
    isTotal : Boolean = false,
    isPromo : Boolean = false
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontSize   = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color      = if (isPromo) SuccessGreen else TextPrimary
        )
        Text(
            text       = value,
            fontSize   = if (isTotal) 18.sp else 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = when {
                isPromo -> SuccessGreen
                isTotal -> RedPrimary
                else    -> TextPrimary
            }
        )
    }
}