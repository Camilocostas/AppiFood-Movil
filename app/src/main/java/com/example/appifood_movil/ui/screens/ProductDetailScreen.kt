package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.graphicsLayer
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    id: Int,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val description by viewModel.description.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadProduct(id)
    }

    var adicionesSeleccionadas by remember { mutableStateOf(setOf<String>()) }
    var cantidad by remember { mutableStateOf(1) }

    // ── ESTADOS PARA ANIMACIONES ──────────────────────────────────
    var isAddingToCart by remember { mutableStateOf(false) }
    var showAddedAnimation by remember { mutableStateOf(false) }
    var buttonScale by remember { mutableStateOf(1f) }
    var cartIconRotation by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

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
                    text = "Cargando producto...",
                    color = TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val productData = product ?: return

    // ── ANIMACIÓN DEL BOTÓN ──────────────────────────────────────
    val animatedButtonScale by animateFloatAsState(
        targetValue = buttonScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    Scaffold(
        modifier = Modifier.graphicsLayer { alpha = screenAlpha },
        bottomBar = {
            Column {
                // ── PANEL DE ACCIÓN ──────────────────────────────────
                Surface(
                    shadowElevation = 12.dp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = if (visible) 0f else 100f
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ── CONTADOR ANIMADO ──────────────────────────
                        AnimatedCounter(
                            cantidad = cantidad,
                            onIncrement = { cantidad++ },
                            onDecrement = { if (cantidad > 1) cantidad-- }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // ── BOTÓN AGREGAR CON ANIMACIÓN EXTRAVAGANTE ──
                        AnimatedAddToCartButton(
                            isAdding = isAddingToCart,
                            showAdded = showAddedAnimation,
                            scale = animatedButtonScale,
                            onClick = {
                                scope.launch {
                                    // Animación de clic
                                    isAddingToCart = true
                                    buttonScale = 0.85f
                                    delay(150)
                                    buttonScale = 1.1f
                                    delay(150)
                                    buttonScale = 1f

                                    // Animación de éxito
                                    showAddedAnimation = true
                                    cartIconRotation = 360f
                                    delay(800)

                                    // Reset
                                    isAddingToCart = false
                                    showAddedAnimation = false
                                    cartIconRotation = 0f
                                }
                            }
                        )
                    }
                }

                // ── FOOTER ──────────────────────────────────────────
                AppiFoodFooter(
                    navController = navController,
                    currentRoute = "home",
                    cartCount = 6,
                    onSearchClick = { navController.navigate("search") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ── HEADER CON IMAGEN ──────────────────────────────────
            item {
                ProductImageHeader(
                    imageRes = productData.imageRes,
                    name = productData.name,
                    price = productData.price,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── DESCRIPCIÓN ────────────────────────────────────────
            item {
                ProductDescriptionSection(
                    description = description
                )
            }

            // ── ADICIONES ──────────────────────────────────────────
            item {
                AnimatedSectionHeader(
                    title = "Personaliza tu pedido",
                    icon = Icons.Default.Edit
                )
            }

            // ── LISTA DE ADICIONES ─────────────────────────────────
            val opciones = listOf("Papas Fritas", "Extra Queso", "Tocino", "Salsa Especial")
            items(opciones) { item ->
                AnimatedAddonOption(
                    text = item,
                    isSelected = adicionesSeleccionadas.contains(item),
                    onToggle = {
                        adicionesSeleccionadas = if (adicionesSeleccionadas.contains(item)) {
                            adicionesSeleccionadas - item
                        } else {
                            adicionesSeleccionadas + item
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// ── HEADER CON IMAGEN ─────────────────────────────────────────────
@Composable
fun ProductImageHeader(
    imageRes: Int,
    name: String,
    price: Double,
    onBack: () -> Unit
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
        // ── IMAGEN CON ANIMACIÓN ──────────────────────────────────
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                },
            contentScale = ContentScale.Crop
        )

        // ── OVERLAY ────────────────────────────────────────────────
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

        // ── CÍRCULOS DECORATIVOS ──────────────────────────────────
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

        // ── BOTÓN VOLVER ───────────────────────────────────────────
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

        // ── BADGE DE CALIFICACIÓN ──────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 54.dp, end = 16.dp)
                .clip(RoundedCornerShape(50)),
            color = Color.White.copy(alpha = 0.3f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = YellowAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "4.8",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── TEXTO INFORMATIVO ──────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            // Tag decorativo
            Surface(
                shape = RoundedCornerShape(50),
                color = YellowAccent,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🔥 OFERTA ESPECIAL",
                    color = RedPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Text(
                text = name,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$ ${String.format("%,.0f", price).replace(",", ".")}",
                    color = YellowAccent,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$ ${String.format("%,.0f", price * 1.3).replace(",", ".")}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    style = androidx.compose.ui.text.TextStyle(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                )
            }
        }
    }
}

// ── CONTADOR ANIMADO ──────────────────────────────────────────────
@Composable
fun AnimatedCounter(
    cantidad: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "counterScale"
    )

    Surface(
        modifier = Modifier
            .height(56.dp)
            .width(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F0F0)),
        color = Color(0xFFF0F0F0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Disminuir",
                    tint = RedPrimary
                )
            }

            Text(
                text = "$cantidad",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Aumentar",
                    tint = RedPrimary
                )
            }
        }
    }
}

// ── BOTÓN AGREGAR AL CARRITO CON ANIMACIÓN EXTRAVAGANTE ────────
// ── BOTÓN AGREGAR AL CARRITO CON ANIMACIÓN EXTRAVAGANTE ────────
// ── BOTÓN AGREGAR AL CARRITO CON ANIMACIÓN EXTRAVAGANTE ────────
// ── BOTÓN AGREGAR AL CARRITO CON ANIMACIÓN EXTRAVAGANTE ────────
@Composable
fun AnimatedAddToCartButton(
    isAdding: Boolean,
    showAdded: Boolean,
    scale: Float,
    onClick: () -> Unit
) {
    // ── ESTADOS DE ANIMACIÓN ──────────────────────────────────────
    var rotation by remember { mutableStateOf(0f) }
    var buttonColor by remember { mutableStateOf(RedPrimary) }
    var buttonText by remember { mutableStateOf("Agregar al carrito") }
    var showIcon by remember { mutableStateOf(true) }

    // ── ANIMACIÓN DE ONDA EXPANSIVA ──────────────────────────────
    var rippleScale by remember { mutableStateOf(0f) }
    var showRipple by remember { mutableStateOf(false) }

    // ── ANIMACIÓN DE CONFETI ──────────────────────────────────────
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(showAdded) {
        if (showAdded) {
            // 1. Rotación del icono
            rotation = 360f

            // 2. Mostrar confeti
            showConfetti = true
            delay(200)

            // 3. Cambiar colores y texto
            buttonColor = Color(0xFF4CAF50) // Verde éxito
            buttonText = "¡Agregado!"
            showIcon = false

            // 4. Onda expansiva
            showRipple = true
            rippleScale = 1f
            delay(300)
            rippleScale = 2f
            delay(200)
            showRipple = false

            delay(300)

            // 5. Resetear a estado normal (después de 2 segundos)
            delay(1200)
            buttonColor = RedPrimary
            buttonText = "Agregar al carrito"
            showIcon = true
            showConfetti = false
        }
    }

    // ── ANIMACIONES ─────────────────────────────────────────────────
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutLinearInEasing
        ),
        label = "iconRotation"
    )

    val rippleAlpha by animateFloatAsState(
        targetValue = if (showRipple) 1f else 0f,
        animationSpec = tween(500),
        label = "rippleAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        // ── ONDA EXPANSIVA ──────────────────────────────────────────
        if (showRipple) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = rippleScale
                        scaleY = rippleScale
                        alpha = rippleAlpha * (1f - (rippleScale - 1f) / 1f)
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(RedPrimary.copy(alpha = 0.3f))
            )
        }

        // ── BOTÓN PRINCIPAL ─────────────────────────────────────────
        Button(
            onClick = {
                if (!isAdding && !showAdded) {
                    onClick()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White,
                disabledContainerColor = RedPrimary.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            enabled = !isAdding && !showAdded
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── ICONO CON ROTACIÓN ──────────────────────────────
                if (showIcon) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                rotationZ = animatedRotation
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    // Icono de check animado
                    AnimatedVisibility(
                        visible = showAdded,
                        enter = fadeIn(animationSpec = tween(300)) +
                                scaleIn(initialScale = 0.5f, animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        Row {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }

                // ── TEXTO ─────────────────────────────────────────────
                if (isAdding) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Agregando...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Texto con animación de entrada/salida
                    AnimatedContent(
                        targetState = buttonText,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) +
                                    slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(300))) togetherWith
                                    (fadeOut(animationSpec = tween(300)) +
                                            slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)))
                        }
                    ) { text ->
                        Text(
                            text = text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── CONFETI ──────────────────────────────────────────────────
        if (showConfetti) {
            ConfettiEffect()
        }
    }
}
@Composable
fun ConfettiEffect() {
    val colors = listOf(
        YellowAccent,
        RedPrimary,
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // ── CONFETI CIRCULAR ──────────────────────────────────────
        repeat(30) { index ->
            val angle = (index * 12).toFloat()
            val distance = (100 + (index % 5) * 20).dp
            val size = (8 + (index % 8)).dp
            val color = colors[index % colors.size]
            val startX = (index * 15 - 225).dp
            val startY = (-100 - (index % 8) * 30).dp

            // ── CALCULAR OFFSET CORRECTAMENTE ──────────────────────
            val offsetMultiplier = (index % 3 - 1)
            val offsetAmount = offsetMultiplier * 150
            val targetX = startX + offsetAmount.dp

            // Animación de caída con movimiento lateral
            val xOffsetAnim by animateDpAsState(
                targetValue = targetX,
                animationSpec = tween(
                    durationMillis = 1200 + (index * 50),
                    easing = FastOutSlowInEasing
                ),
                label = "confetti_x_$index"
            )

            val yOffsetAnim by animateDpAsState(
                targetValue = startY + 600.dp,
                animationSpec = tween(
                    durationMillis = 1200 + (index * 50),
                    easing = FastOutSlowInEasing
                ),
                label = "confetti_y_$index"
            )

            val rotationAnim by animateFloatAsState(
                targetValue = (index * 60).toFloat() + 720f,
                animationSpec = tween(
                    durationMillis = 1200 + (index * 50),
                    easing = LinearEasing
                ),
                label = "confetti_rotation_$index"
            )

            // ── CONFITI ──────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .offset(x = xOffsetAnim + 200.dp, y = yOffsetAnim)
                    .size(size)
                    .graphicsLayer {
                        rotationZ = rotationAnim
                        alpha = 1f - (yOffsetAnim.value / 1000f)
                    },
                shape = if (index % 2 == 0) CircleShape else RoundedCornerShape(2.dp),
                color = color,
                shadowElevation = 4.dp
            ) {}
        }

        // ── ESTRELLAS ──────────────────────────────────────────────
        repeat(10) { index ->
            val size = (4 + (index % 6)).dp
            val color = Color.White.copy(alpha = 0.7f)

            val starX = (-150 + (index * 35)).dp
            val starY = (-50 + (index * 20)).dp + 500.dp

            val xOffsetAnim by animateDpAsState(
                targetValue = starX,
                animationSpec = tween(
                    durationMillis = 800 + (index * 30),
                    easing = FastOutSlowInEasing
                ),
                label = "star_x_$index"
            )

            val yOffsetAnim by animateDpAsState(
                targetValue = starY,
                animationSpec = tween(
                    durationMillis = 800 + (index * 30),
                    easing = FastOutSlowInEasing
                ),
                label = "star_y_$index"
            )

            val scaleAnim by animateFloatAsState(
                targetValue = 1f + (index % 2) * 0.5f,
                animationSpec = tween(
                    durationMillis = 400 + (index * 30),
                    easing = FastOutSlowInEasing
                ),
                label = "star_scale_$index"
            )

            Box(
                modifier = Modifier
                    .offset(x = xOffsetAnim + 200.dp, y = yOffsetAnim)
                    .size(size)
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                        alpha = 0.8f
                    }
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// ── SECCIÓN DE DESCRIPCIÓN ──────────────────────────────────────
@Composable
fun ProductDescriptionSection(description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .graphicsLayer {
                translationY = 0f
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

// ── SECTION HEADER CON ÍCONO ──────────────────────────────────────
@Composable
fun AnimatedSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
                .background(RedPrimary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

// ── OPCIÓN DE ADICIÓN ANIMADA ────────────────────────────────────
@Composable
fun AnimatedAddonOption(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "addonScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) RedPrimary.copy(alpha = 0.08f) else Color.White,
        animationSpec = tween(200),
        label = "addonBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) RedPrimary else Color(0xFFE0E0E0),
        animationSpec = tween(200),
        label = "addonBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono decorativo
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = if (isSelected) RedPrimary else Color(0xFFF0F0F0)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextMuted
            )

            // Precio de adición
            Text(
                text = "+ $2.500",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) RedPrimary else TextMuted
            )
        }
    }
}