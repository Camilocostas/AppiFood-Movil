// ui/screens/OrderFormBottomSheet.kt
package com.example.appifood_movil.ui.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appifood_movil.data.model.PaymentMethod
import com.example.appifood_movil.data.model.RestaurantInfo
import com.example.appifood_movil.service.LocalNotificationService
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.viewmodel.OrderViewModel

private val RedPrimary   = Color(0xFFD32F2F)
private val YellowAccent = Color(0xFFFFD600)
private val SuccessGreen = Color(0xFF1D9E75)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
private val FieldBg      = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormBottomSheet(
    sheetState     : SheetState,
    cartViewModel  : CartViewModel,
    authViewModel  : AuthViewModel,
    orderViewModel : OrderViewModel,
    restaurantInfo : RestaurantInfo = RestaurantInfo(id = 1, name = "Restaurante AppiFood", phone = "3001234567"),
    onDismiss      : () -> Unit,
    onOrderPlaced  : (orderId: String) -> Unit
) {
    val context = LocalContext.current
    val notificationService = remember { LocalNotificationService(context) }

    val userData       by authViewModel.userData.collectAsState()
    val paymentMethods by authViewModel.paymentMethods.collectAsState()
    val isLoading      by orderViewModel.isLoading.collectAsState()
    val error          by orderViewModel.error.collectAsState()
    val user           by authViewModel.user.collectAsState()

    // Cargar métodos de pago si no están cargados
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            authViewModel.loadPaymentMethods(uid)
            if (authViewModel.userData.value == null) {
                authViewModel.getUserDataFromFirestore(uid)
            }
        }
    }

    // ── Estados del formulario ────────────────────────────────────
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf<PaymentMethod?>(null) }
    var useCash by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf(false) }

    // Preseleccionar método predeterminado
    LaunchedEffect(paymentMethods) {
        if (selectedPayment == null) {
            selectedPayment = paymentMethods.find { it.isDefault } ?: paymentMethods.firstOrNull()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color.White,
        shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            // ── Header del sheet ──────────────────────────────────
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(YellowAccent)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "Confirmar pedido",
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text     = "Revisa tus datos antes de confirmar",
                fontSize = 14.sp,
                color    = TextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sección: Datos del cliente (precargados) ──────────
            FormSectionLabel(icon = "👤", title = "Tus datos")

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = FieldBg),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormReadOnlyRow(
                        label = "Nombre",
                        value = "${userData?.names ?: ""} ${userData?.lastNames ?: ""}".trim()
                            .ifEmpty { "No registrado" }
                    )
                    HorizontalDivider(color = Color.White, thickness = 1.dp)
                    FormReadOnlyRow(
                        label = "Teléfono",
                        value = userData?.phone?.ifEmpty { "No registrado" } ?: "No registrado",
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sección: Dirección de entrega ─────────────────────
            FormSectionLabel(icon = "📍", title = "Dirección de entrega")

            OutlinedTextField(
                value         = deliveryAddress,
                onValueChange = {
                    deliveryAddress = it
                    addressError    = false
                },
                placeholder   = { Text("Ej: Calle 5 # 10-20, Popayán") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                isError       = addressError,
                supportingText = if (addressError) {
                    { Text("Ingresa una dirección de entrega", color = RedPrimary) }
                } else null,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = RedPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    errorBorderColor     = RedPrimary
                ),
                leadingIcon   = {
                    Icon(Icons.Default.LocationOn, null, tint = TextMuted)
                },
                singleLine    = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sección: Método de pago ───────────────────────────
            FormSectionLabel(icon = "💳", title = "Método de pago")

            if (paymentError) {
                Text(
                    text     = "Selecciona un método de pago",
                    color    = RedPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Métodos de Firebase
            paymentMethods.forEach { method ->
                PaymentOptionCard(
                    icon       = method.getIcon(),
                    title      = method.type,
                    subtitle   = method.identifier,
                    isSelected = selectedPayment?.id == method.id && !useCash,
                    onClick    = {
                        selectedPayment = method
                        useCash         = false
                        paymentError    = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Opción efectivo siempre disponible
            PaymentOptionCard(
                icon       = "💵",
                title      = "Efectivo contra entrega",
                subtitle   = "Paga cuando llegue tu pedido",
                isSelected = useCash,
                onClick    = {
                    useCash          = true
                    selectedPayment  = null
                    paymentError     = false
                }
            )

            // Error del ViewModel
            if (!error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFEBEB)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = RedPrimary,
                            modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(error ?: "", color = RedPrimary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Resumen rápido del total ──────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.06f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Total a pagar", fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = TextPrimary)
                    Text(
                        orderViewModel.formatCurrency(cartViewModel.total),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 20.sp,
                        color      = RedPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Botón confirmar ───────────────────────────────────
            Button(
                onClick = {
                    // Validaciones
                    addressError  = deliveryAddress.isBlank()
                    paymentError  = selectedPayment == null && !useCash
                    if (addressError || paymentError) return@Button

                    val paymentMethodStr = if (useCash) "Efectivo"
                    else selectedPayment?.type ?: "Efectivo"
                    val paymentDetail    = if (useCash) ""
                    else selectedPayment?.identifier ?: ""

                    // Construir el pedido
                    orderViewModel.buildOrder(
                        userData        = userData,
                        restaurantInfo  = restaurantInfo,
                        cartItems       = cartViewModel.cartItems.toList(),
                        deliveryAddress = deliveryAddress.trim(),
                        paymentMethod   = paymentMethodStr,
                        paymentDetail   = paymentDetail,
                        shipping        = cartViewModel.shipping
                    )

                    // Guardar en Firestore
                    orderViewModel.confirmAndSaveOrder { orderId ->
                        // ✅ Notificación local inmediata
                        notificationService.showOrderNotification(
                            orderId = orderId,
                            restaurantName = restaurantInfo.name,
                            status = "preparing",
                            address = deliveryAddress.trim()
                        )

                        cartViewModel.clearCart()
                        onOrderPlaced(orderId)
                    }
                },
                enabled   = !isLoading,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(18.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor         = RedPrimary,
                    contentColor           = Color.White,
                    disabledContainerColor = Color(0xFFE57373)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White,
                        strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Procesando...", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                } else {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Confirmar pedido", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// ── Componentes internos del sheet ────────────────────────────────

@Composable
private fun FormSectionLabel(icon: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(bottom = 10.dp)
    ) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

@Composable
private fun FormReadOnlyRow(label: String, value: String, isLast: Boolean = false) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMuted, fontSize = 13.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        }
        if (!isLast) HorizontalDivider(color = Color.White, thickness = 1.dp)
    }
}

@Composable
private fun PaymentOptionCard(
    icon       : String,
    title      : String,
    subtitle   : String,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue   = if (isSelected) RedPrimary else Color(0xFFE0E0E0),
        animationSpec = tween(200),
        label         = "payBorder"
    )
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) RedPrimary.copy(alpha = 0.06f) else Color.White,
        animationSpec = tween(200),
        label         = "payBg"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        border    = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextMuted)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null,
                    tint = RedPrimary, modifier = Modifier.size(22.dp))
            }
        }
    }
}