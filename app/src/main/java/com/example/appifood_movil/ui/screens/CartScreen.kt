package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.components.OrderReceiptScreen
import com.example.appifood_movil.ui.components.CartItemCard
import com.example.appifood_movil.ui.components.PaymentOptionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = viewModel()) {
    val appiFoodRed = Color(0xFFFF4B3A)

    if (viewModel.showReceipt.value) {
        OrderReceiptScreen(
            onClose = { viewModel.showReceipt.value = false; navController.navigate("home") },
            restaurantName = "Burger House",
            items = viewModel.cartItems,
            paymentMethod = if (viewModel.paymentDetail.value.isNotEmpty()) "${viewModel.selectedPayment.value} (${viewModel.paymentDetail.value})" else viewModel.selectedPayment.value,
            totalAmount = viewModel.total,
            estimatedTime = "25 - 35 min"
        )
    } else {
        Scaffold { padding ->
            Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(padding).padding(horizontal = 20.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Mi carrito", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Vaciar", color = appiFoodRed, modifier = Modifier.clickable { viewModel.cartItems.clear() })
                }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(viewModel.cartItems) { item ->
                        CartItemCard(item.name, "Burger House", item.totalPrice, R.drawable.bicmac, item.quantity)
                    }
                }
                Button(onClick = { viewModel.showPaySheet.value = true }, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = appiFoodRed), shape = RoundedCornerShape(15.dp)) {
                    Text("Confirmar pedido")
                }
            }
        }
    }

    if (viewModel.showPaySheet.value) {
        ModalBottomSheet(onDismissRequest = { viewModel.showPaySheet.value = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                listOf("Efectivo", "Nequi", "Daviplata", "Bancolombia", "PayPal").forEach { method ->
                    PaymentOptionRow(method, viewModel.selectedPayment.value == method, viewModel.paymentDetail.value, { viewModel.paymentDetail.value = it }, { viewModel.selectedPayment.value = method })
                }
                Button(onClick = { viewModel.showPaySheet.value = false; viewModel.showReceipt.value = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp).height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = appiFoodRed)) {
                    Text("Pagar ahora")
                }
            }
        }
    }
}