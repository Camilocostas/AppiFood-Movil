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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.PaymentMethod
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
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
fun PaymentsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val user by authViewModel.user.collectAsState()
    val paymentMethods by authViewModel.paymentMethods.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    // ── ESTADOS ──────────────────────────────────────────────────────
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // ── ANIMACIÓN DE ENTRADA ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "paymentsFadeIn"
    )

    LaunchedEffect(Unit) {
        visible = true
        user?.uid?.let { uid ->
            authViewModel.loadPaymentMethods(uid)
        }
    }

    Scaffold(
        modifier = Modifier.graphicsLayer { alpha = screenAlpha },
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            AnimatedPaymentsTopBar(
                onBack = { navController.popBackStack() },
                onAddPayment = { showAddPaymentDialog = true }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (paymentMethods.isEmpty() && !isLoading) {
                // ── SIN MÉTODOS DE PAGO ──────────────────────────────
                AnimatedEmptyPayments(
                    onAddPayment = { showAddPaymentDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── TARJETA DE RESUMEN ────────────────────────────
                    item {
                        PaymentSummaryCard(
                            totalMethods = paymentMethods.size,
                            defaultMethod = paymentMethods.find { it.isDefault }
                        )
                    }

                    // ── LISTA DE MÉTODOS ──────────────────────────────
                    items(paymentMethods) { method ->
                        AnimatedPaymentMethodItem(
                            method = method,
                            isDefault = method.isDefault,
                            onSetDefault = {
                                user?.uid?.let { uid ->
                                    authViewModel.setDefaultPaymentMethod(uid, method.id) { success ->
                                        if (success) {
                                            scope.launch {
                                                showSuccessAnimation = true
                                                delay(1000)
                                                showSuccessAnimation = false
                                            }
                                        }
                                    }
                                }
                            },
                            onDelete = {
                                user?.uid?.let { uid ->
                                    isDeleting = true
                                    authViewModel.removePaymentMethod(uid, method.id) { success ->
                                        isDeleting = false
                                        if (success) {
                                            scope.launch {
                                                showSuccessAnimation = true
                                                delay(1000)
                                                showSuccessAnimation = false
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }

            // ── LOADING ──────────────────────────────────────────────────
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = YellowAccent,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // ── ANIMACIÓN DE ÉXITO ──────────────────────────────────
            AnimatedVisibility(
                visible = showSuccessAnimation,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 1.2f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(220.dp, 180.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(RedPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "¡Actualizado!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // ── DIÁLOGO PARA AGREGAR MÉTODO DE PAGO ──────────────────────
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
                                delay(1000)
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

// ── TOP BAR ANIMADA ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedPaymentsTopBar(
    onBack: () -> Unit,
    onAddPayment: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "💳 Mis Métodos de Pago",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        navigationIcon = {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            Button(
                onClick = onAddPayment,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar", fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White
        )
    )
}

// ── RESUMEN DE PAGOS ──────────────────────────────────────────────
@Composable
fun PaymentSummaryCard(
    totalMethods: Int,
    defaultMethod: PaymentMethod?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "💳 Métodos guardados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    text = "$totalMethods método(s) de pago",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                if (defaultMethod != null) {
                    Text(
                        text = "Predeterminado: ${defaultMethod.type}",
                        color = RedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = RedPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$totalMethods",
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// ── ITEM DE MÉTODO DE PAGO ANIMADO ──────────────────────────────
@Composable
fun AnimatedPaymentMethodItem(
    method: PaymentMethod,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "methodScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { isPressed = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDefault) RedPrimary.copy(alpha = 0.05f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDefault) 4.dp else 2.dp)
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
                    // ── ICONO ──────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(RedPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = method.getIcon(),
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = method.type,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            if (isDefault) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = RedPrimary
                                ) {
                                    Text(
                                        text = "Predeterminado",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = method.identifier,
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Titular: ${method.holderName}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ── ACCIONES ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDefault) {
                    TextButton(
                        onClick = onSetDefault,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = RedPrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Establecer por defecto",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Establecer por defecto", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── ESTADO VACÍO ANIMADO ──────────────────────────────────────────
@Composable
fun AnimatedEmptyPayments(onAddPayment: () -> Unit) {
    var bounce by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (bounce) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "emptyScale"
    )

    LaunchedEffect(Unit) {
        bounce = true
        delay(1000)
        bounce = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(RedPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💳",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No tienes métodos de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Agrega un método de pago para realizar pedidos fácilmente",
                    color = TextMuted,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAddPayment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar método de pago",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── DIÁLOGO PARA AGREGAR MÉTODO DE PAGO ──────────────────────────
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

    val paymentTypes = listOf("Nequi", "Bancolombia", "PSE", "Daviplata")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar método de pago",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                // ── TIPO DE PAGO ──────────────────────────────────────
                Text(
                    text = "Tipo de pago",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentTypes.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    text = type,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedPrimary.copy(alpha = 0.1f),
                                selectedLabelColor = RedPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── IDENTIFICADOR ─────────────────────────────────────
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("Identificador") },
                    placeholder = {
                        when (selectedType) {
                            "Nequi" -> Text("Número de celular")
                            "Bancolombia" -> Text("Número de cuenta")
                            "PSE" -> Text("Número de cuenta o correo")
                            "Daviplata" -> Text("Número de celular")
                            else -> Text("Identificador")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        cursorColor = RedPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── NOMBRE DEL TITULAR ──────────────────────────────
                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it },
                    label = { Text("Nombre del titular") },
                    placeholder = { Text("Como aparece en la cuenta") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        cursorColor = RedPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── PREDETERMINADO ────────────────────────────────────
                Row(
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Establecer como método predeterminado",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (identifier.isNotBlank() && holderName.isNotBlank()) {
                        val paymentMethod = PaymentMethod(
                            type = selectedType,
                            identifier = identifier,
                            holderName = holderName,
                            isDefault = isDefault
                        )
                        onAdd(paymentMethod)
                    }
                },
                enabled = !isLoading && identifier.isNotBlank() && holderName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar", color = TextMuted)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}