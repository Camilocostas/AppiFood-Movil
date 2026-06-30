// ui/screens/OrderConfirmationScreen.kt
package com.example.appifood_movil.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.example.appifood_movil.service.LocalNotificationService
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.OrderViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val SuccessGreen = Color(0xFF1D9E75)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@Composable
fun OrderConfirmationScreen(
    navController  : NavController,
    orderId        : String,
    orderViewModel : OrderViewModel = hiltViewModel()
) {
    val savedOrder by orderViewModel.savedOrder.collectAsState()
    val isLoading  by orderViewModel.isLoading.collectAsState()
    val error      by orderViewModel.error.collectAsState()

    LaunchedEffect(orderId) {
        if (orderId.isNotBlank()) {
            orderViewModel.loadOrderById(orderId)
        }
    }

    val context = LocalContext.current
    val notificationService = remember { LocalNotificationService(context) }

    LaunchedEffect(savedOrder) {
        if (savedOrder != null) {
            notificationService.showOrderNotification(
                orderId = orderId,
                restaurantName = savedOrder?.restaurant?.nombre ?: "Restaurante",
                status = "preparing",
                address = savedOrder?.deliveryAddress ?: "Tu dirección"
            )
        }
    }

    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "confirmFade"
    )
    LaunchedEffect(Unit) { visible = true }

    var checkVisible by remember { mutableStateOf(false) }
    val checkScale by animateFloatAsState(
        targetValue   = if (checkVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "checkScale"
    )
    LaunchedEffect(Unit) {
        delay(300)
        checkVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val ringPulse by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "ringPulse"
    )

    val qrBitmap = remember(orderId) { generateQrBitmap(orderId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep)))
            .graphicsLayer { alpha = screenAlpha }
    ) {
        // ✅ Nombre corregido para evitar ambigüedad
        OrderConfirmDecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── HEADER ANIMADO ────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier         = Modifier
                        .size(100.dp)
                        .scale(checkScale * ringPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(YellowAccent, Color(0xFFFF8F00))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check, null,
                        tint     = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "¡Pedido confirmado!", fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold, color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.width(48.dp).height(3.dp)
                        .clip(RoundedCornerShape(50)).background(YellowAccent)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tu pedido está en camino 🛵",
                    fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text       = "Pedido #$orderId",
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }

            // ── CUERPO DEL RECIBO ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp, bottom = 40.dp)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = RedPrimary)
                            Spacer(Modifier.height(16.dp))
                            Text("Cargando comprobante...", color = TextMuted)
                        }
                    }

                    savedOrder != null -> {
                        ReceiptContent(
                            order          = savedOrder!!,
                            qrBitmap       = qrBitmap,
                            orderId        = orderId,
                            orderViewModel = orderViewModel,
                            navController  = navController
                        )
                    }

                    else -> {
                        Text(
                            text = "Error al cargar los datos del pedido",
                            modifier = Modifier.align(Alignment.Center).padding(48.dp),
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptContent(
    order          : Order,
    qrBitmap       : Bitmap?,
    orderId        : String,
    orderViewModel : OrderViewModel,
    navController  : NavController
) {
    Column {
        Box(
            modifier = Modifier.width(40.dp).height(3.dp)
                .clip(RoundedCornerShape(50)).background(YellowAccent)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            "Comprobante del pedido",
            fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary
        )
        Text(
            orderViewModel.formatDate(order.timestamp),
            fontSize = 13.sp, color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Datos del cliente ─────────────────────────────────────
        ReceiptSection(title = "👤 Cliente") {
            ReceiptRow("Nombre",    order.customer.fullName.ifBlank { "No registrado" })
            ReceiptRow("Teléfono",  order.customer.phone.ifBlank    { "No registrado" })
            ReceiptRow("Dirección", order.deliveryAddress.ifBlank   { "No registrada" }, isLast = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Datos del restaurante ─────────────────────────────────
        ReceiptSection(title = "🏪 Restaurante") {
            ReceiptRow("Nombre",    order.restaurant.nombre.ifBlank  { "No registrado" })
            ReceiptRow("Teléfono",  order.restaurant.telefono.ifBlank { "No registrado" }, isLast = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Productos ─────────────────────────────────────────────
        ReceiptSection(title = "🍔 Productos") {
            order.items.forEachIndexed { index, item ->
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("x${item.quantity} × ${orderViewModel.formatCurrency(item.price)}", fontSize = 12.sp, color = TextMuted)
                    }
                    Text(orderViewModel.formatCurrency(item.subtotal), fontWeight = FontWeight.Bold)
                }
                if (index < order.items.lastIndex) HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Resumen de pago ───────────────────────────────────────
        ReceiptSection(title = "💰 Resumen de pago") {
            ReceiptRow("Subtotal",  orderViewModel.formatCurrency(order.subtotal))
            ReceiptRow("Domicilio", orderViewModel.formatCurrency(order.shipping))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(orderViewModel.formatCurrency(order.total), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = RedPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            ReceiptRow("Método", order.payment.method.ifBlank { "No especificado" }, isLast = true)
        }

        Spacer(modifier = Modifier.height(24.dp))

        QrSection(qrBitmap = qrBitmap, orderId = orderId)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Pill tiempo estimado ──────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            color    = SuccessGreen.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Timer, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tiempo estimado: 30-45 min", color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botón volver al inicio ────────────────────────────────
        Button(
            onClick   = {
                orderViewModel.clearOrder()
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier  = Modifier.fillMaxWidth().height(54.dp),
            shape     = RoundedCornerShape(18.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Icon(Icons.Default.Home, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ReceiptSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RedPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isLast: Boolean = false) {
    Column {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextMuted, fontSize = 13.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        if (!isLast) HorizontalDivider(color = Color.White, thickness = 1.dp)
    }
}

@Composable
private fun QrSection(qrBitmap: Bitmap?, orderId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📱 Código QR del pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR",
                    modifier = Modifier.size(200.dp).clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pedido #$orderId", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

private fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, mapOf(EncodeHintType.MARGIN to 1))
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (bits[x, y]) 0xFF1A1A1A.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        bmp
    } catch (e: Exception) { null }
}

@Composable
fun OrderConfirmDecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.size(280.dp).offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier.size(200.dp).align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 40.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.04f))
        )
    }
}
