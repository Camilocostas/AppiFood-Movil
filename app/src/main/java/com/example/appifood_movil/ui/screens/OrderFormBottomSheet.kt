package com.example.appifood_movil.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appifood_movil.data.model.ReceiptItem
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.example.appifood_movil.ui.viewmodel.OrderViewModel
import com.example.appifood_movil.ui.viewmodel.RestaurantInfo as ViewModelRestaurantInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormBottomSheet(
    onDismiss       : () -> Unit,
    onOrderConfirmed: (String) -> Unit,
    restaurantInfo  : ViewModelRestaurantInfo,
    cartItems       : List<ReceiptItem>,
    shipping        : Int,
    authViewModel   : AuthViewModel = hiltViewModel(),
    orderViewModel  : OrderViewModel = hiltViewModel()
) {
    val userData by authViewModel.userData.collectAsState()
    val paymentMethods by authViewModel.paymentMethods.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()

    var selectedAddress by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.getMe()
        authViewModel.loadPaymentMethods("0")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        dragHandle       = { BottomSheetDefaults.DragHandle(color = Color(0xFFE0E0E0)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                "Finalizar Pedido",
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Dirección ─────────────────────────────────────────
            FormSectionLabel(icon = "📍", title = "Dirección de entrega")
            OutlinedTextField(
                value         = selectedAddress,
                onValueChange = { selectedAddress = it },
                placeholder   = { Text("Ingresa tu dirección") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Método de Pago ────────────────────────────────────
            FormSectionLabel(icon = "💳", title = "Método de pago")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                paymentMethods.forEach { method ->
                    PaymentChip(
                        text     = "${method.getIcon()} ${method.type}",
                        isSelected = selectedPayment == method.id,
                        onClick    = { selectedPayment = method.id }
                    )
                }
                PaymentChip(
                    text     = "💵 Efectivo",
                    isSelected = selectedPayment == "cash",
                    onClick    = { selectedPayment = "cash" }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón de Confirmar ────────────────────────────────
            Button(
                onClick = {
                    orderViewModel.buildOrder(
                        userData        = userData,
                        restaurantInfo  = restaurantInfo,
                        cartItems       = cartItems,
                        deliveryAddress = selectedAddress,
                        paymentMethod   = selectedPayment ?: "Efectivo",
                        paymentDetail   = "Pago contra entrega",
                        shipping        = shipping
                    )
                    orderViewModel.confirmAndSaveOrder { orderId ->
                        onOrderConfirmed(orderId)
                    }
                },
                enabled  = selectedAddress.isNotBlank() && selectedPayment != null && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Pedido", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FormSectionLabel(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun PaymentChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape    = RoundedCornerShape(50),
        color    = if (isSelected) Color(0xFFFFD600) else Color(0xFFF5F5F5),
        border   = BorderStroke(1.dp, if (isSelected) Color(0xFFFFD600) else Color(0xFFE0E0E0))
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
