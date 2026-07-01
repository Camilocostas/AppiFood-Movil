package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.PaymentMethod
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Paleta unificada AppiFood ─────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF8A8A8A)
private val BgScreen     = Color(0xFFF6F3F0)

// ── Identidad visual real de cada método de pago ──────────────────
private data class PaymentBrand(
    val label: String,
    val short: String,
    val primary: Color,
    val secondary: Color,
    val onBrand: Color = Color.White
)

private val brandMap = mapOf(
    "Nequi"        to PaymentBrand("Nequi",        "N",   Color(0xFF1B0F47), Color(0xFFE91C7D)),
    "Daviplata"    to PaymentBrand("Daviplata",     "D",   Color(0xFFEE2A24), Color(0xFFB71B1B)),
    "Bancolombia"  to PaymentBrand("Bancolombia",   "B",   Color(0xFFFFD100), Color(0xFFFCAA00), onBrand = Color(0xFF1A1A1A)),
    "PSE"          to PaymentBrand("PSE",           "PSE", Color(0xFF00255D), Color(0xFF0057B8))
)

private fun brandFor(type: String) =
    brandMap[type] ?: PaymentBrand(type, type.take(1).uppercase(), RedPrimary, RedDark)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val user by authViewModel.user.collectAsState()
    val paymentMethods by authViewModel.paymentMethods.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(450, easing = FastOutSlowInEasing),
        label = "paymentsFadeIn"
    )

    LaunchedEffect(Unit) {
        visible = true
        user?.uid?.let { uid -> authViewModel.loadPaymentMethods(uid) }
    }

    Scaffold(
        modifier = Modifier.graphicsLayer { alpha = screenAlpha },
        containerColor = BgScreen,
        topBar = {
            AnimatedPaymentsTopBar(
                onBack = { navController.popBackStack() },
                onAddPayment = { showAddPaymentDialog = true }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (paymentMethods.isEmpty() && !isLoading) {
                AnimatedEmptyPayments(onAddPayment = { showAddPaymentDialog = true })
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PaymentSummaryCard(
                            totalMethods = paymentMethods.size,
                            defaultMethod = paymentMethods.find { it.isDefault }
                        )
                    }

                    itemsIndexed(paymentMethods) { index, method ->
                        StaggeredEntry(index = index) {
                            AnimatedPaymentMethodItem(
                                method = method,
                                isDefault = method.isDefault,
                                onSetDefault = {
                                    user?.uid?.let { uid ->
                                        authViewModel.setDefaultPaymentMethod(uid, method.id) { success ->
                                            if (success) scope.launch {
                                                showSuccessAnimation = true
                                                delay(1100)
                                                showSuccessAnimation = false
                                            }
                                        }
                                    }
                                },
                                onDelete = {
                                    user?.uid?.let { uid ->
                                        authViewModel.removePaymentMethod(uid, method.id) { success ->
                                            if (success) scope.launch {
                                                showSuccessAnimation = true
                                                delay(1100)
                                                showSuccessAnimation = false
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YellowAccent, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
                }
            }

            SuccessOverlay(visible = showSuccessAnimation)
        }
    }

    if (showAddPaymentDialog) {
        AddPaymentMethodDialog(
            onDismiss = { showAddPaymentDialog = false },
            onAdd = { paymentMethod ->
                user?.uid?.let { uid ->
                    authViewModel.addPaymentMethod(uid, paymentMethod) { success ->
                        if (success) {
                            showAddPaymentDialog = false
                            scope.launch {
                                showSuccessAnimation = true
                                delay(1100)
                                showSuccessAnimation = false
                            }
                        }
                    }
                }
            },
            isLoading = isLoading
        )
    }
}

// ── Entrada escalonada para items de lista ─────────────────────────
@Composable
private fun StaggeredEntry(index: Int, content: @Composable () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60L * index)
        shown = true
    }
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(tween(350)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(380, easing = FastOutSlowInEasing)
        )
    ) {
        content()
    }
}

// ── TOP BAR con degradado de marca ──────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedPaymentsTopBar(
    onBack: () -> Unit,
    onAddPayment: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(RedPrimary, RedDark, RedDeep))
            )
            .statusBarsPadding()
    ) {
        // Círculos decorativos translúcidos, fieles a la identidad de AppiFood
        Box(
            modifier = Modifier
                .size(90.dp)
                .graphicsLayer { translationX = 280f; translationY = -30f }
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    color = Color.White.copy(alpha = 0.16f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Métodos de pago", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Gestiona tus formas de pago", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = RedDeep,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { onAddPayment() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

// ── RESUMEN ───────────────────────────────────────────────────────
@Composable
fun PaymentSummaryCard(totalMethods: Int, defaultMethod: PaymentMethod?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Métodos guardados", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text("$totalMethods método(s) registrados", color = TextMuted, fontSize = 13.sp)
                if (defaultMethod != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val brand = brandFor(defaultMethod.type)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(brand.primary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Predeterminado: ${defaultMethod.type}", color = RedPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Surface(
                shape = CircleShape,
                color = RedPrimary.copy(alpha = 0.08f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("$totalMethods", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}

// ── BADGE de marca animado (logo estilizado, sin necesidad de assets) ──
@Composable
private fun BrandBadge(type: String, size: androidx.compose.ui.unit.Dp = 52.dp, pulsing: Boolean = false) {
    val brand = brandFor(type)
    val infinite = rememberInfiniteTransition(label = "badgePulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = if (pulsing) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseAnim"
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(brand.primary, brand.secondary)))
            .shadow(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = brand.short,
            color = brand.onBrand,
            fontWeight = FontWeight.Black,
            fontSize = if (brand.short.length > 1) 14.sp else 20.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// ── ITEM DE MÉTODO DE PAGO ──────────────────────────────────────────
@Composable
fun AnimatedPaymentMethodItem(
    method: PaymentMethod,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val brand = brandFor(method.type)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "methodScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable {
                isPressed = true
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDefault) 6.dp else 2.dp),
        border = if (isDefault) BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(RedPrimary, YellowAccent))) else null
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(120)
                isPressed = false
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandBadge(type = method.type, pulsing = isDefault)
                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(method.type, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        if (isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = YellowAccent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = RedDeep, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Predeterminado", color = RedDeep, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(method.identifier, color = TextMuted, fontSize = 13.sp)
                    Text("Titular: ${method.holderName}", color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0EDEA))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDefault) {
                    TextButton(onClick = onSetDefault, colors = ButtonDefaults.textButtonColors(contentColor = RedPrimary)) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Predeterminar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextMuted, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

// ── ESTADO VACÍO ──────────────────────────────────────────────────
@Composable
fun AnimatedEmptyPayments(onAddPayment: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "emptyFloat")
    val floatY by infinite.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatAnim"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 44.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer { translationY = floatY }
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(RedPrimary.copy(alpha = 0.12f), YellowAccent.copy(alpha = 0.18f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(50.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Aún no tienes métodos de pago", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Agrega Nequi, Daviplata, Bancolombia o PSE para pagar tus pedidos en segundos",
                    color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAddPayment,
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f).height(50.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar método de pago",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ── OVERLAY DE ÉXITO ─────────────────────────────────────────────
@Composable
private fun SuccessOverlay(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 1.15f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(230.dp, 190.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(RedPrimary, RedDeep))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¡Actualizado!", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                }
            }
        }
    }
}

// ── DIÁLOGO PARA AGREGAR MÉTODO DE PAGO ──────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentMethodDialog(
    onDismiss: () -> Unit,
    onAdd: (PaymentMethod) -> Unit,
    isLoading: Boolean
) {
    var selectedType by remember { mutableStateOf("Nequi") }
    var identifier by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val paymentTypes = listOf("Nequi", "Daviplata", "Bancolombia", "PSE")

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(280)) + scaleIn(initialScale = 0.92f, animationSpec = tween(280, easing = FastOutSlowInEasing))
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {

                    // ── Encabezado con degradado ──────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(RedPrimary, RedDark, RedDeep)),
                                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                            )
                            .padding(horizontal = 22.dp, vertical = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .graphicsLayer { translationX = 260f; translationY = -20f }
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        )
                        Column {
                            Text("Nuevo método de pago", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                            Text("Elige tu billetera o banco favorito", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {

                        Text("Tipo de pago", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(paymentTypes) { type ->
                                PaymentTypeOption(
                                    type = type,
                                    selected = selectedType == type,
                                    onClick = { selectedType = type }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = identifier,
                            onValueChange = { identifier = it },
                            label = { Text("Identificador") },
                            placeholder = {
                                Text(
                                    when (selectedType) {
                                        "Nequi", "Daviplata" -> "Número de celular"
                                        "Bancolombia" -> "Número de cuenta"
                                        "PSE" -> "Número de cuenta o correo"
                                        else -> "Identificador"
                                    }
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RedPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPrimary,
                                focusedLabelColor = RedPrimary,
                                cursorColor = RedPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text("Nombre del titular") },
                            placeholder = { Text("Como aparece en la cuenta") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RedPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPrimary,
                                focusedLabelColor = RedPrimary,
                                cursorColor = RedPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDefault) RedPrimary.copy(alpha = 0.06f) else Color(0xFFF7F5F3),
                            modifier = Modifier.fillMaxWidth().clickable { isDefault = !isDefault }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = isDefault,
                                    onCheckedChange = { isDefault = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = RedPrimary,
                                        checkedTrackColor = RedPrimary.copy(alpha = 0.5f),
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = TextMuted.copy(alpha = 0.3f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Establecer como predeterminado", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                enabled = !isLoading,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    if (identifier.isNotBlank() && holderName.isNotBlank()) {
                                        onAdd(
                                            PaymentMethod(
                                                type = selectedType,
                                                identifier = identifier,
                                                holderName = holderName,
                                                isDefault = isDefault
                                            )
                                        )
                                    }
                                },
                                enabled = !isLoading && identifier.isNotBlank() && holderName.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = Color.White),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1.2f).height(48.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Agregar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Opción de tipo de pago (selector animado con badge de marca) ───
@Composable
private fun PaymentTypeOption(type: String, selected: Boolean, onClick: () -> Unit) {
    val brand = brandFor(type)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "typeScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable { onClick() }
            .width(74.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selected) Brush.linearGradient(listOf(brand.primary, brand.secondary))
                    else Brush.linearGradient(listOf(Color(0xFFF0EDEA), Color(0xFFF0EDEA)))
                )
                .then(
                    if (selected) Modifier.shadow(8.dp, RoundedCornerShape(18.dp), spotColor = brand.primary)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = brand.short,
                color = if (selected) brand.onBrand else TextMuted,
                fontWeight = FontWeight.Black,
                fontSize = if (brand.short.length > 1) 13.sp else 19.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = type,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) RedPrimary else TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        AnimatedVisibility(visible = selected) {
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(RedPrimary)
            )
        }
    }
}