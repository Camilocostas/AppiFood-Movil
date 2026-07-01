package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.example.appifood_movil.ui.viewmodel.OrderViewModel
import com.example.appifood_movil.R
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.ReceiptItem
import com.example.appifood_movil.navigation.Screen

private val RedPrimary = Color(0xFFD32F2F)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextMuted = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val shipping = if (subtotal > 0) 3500 else 0
    val total = subtotal + shipping
    val restaurantName  by cartViewModel.restaurantName.collectAsState()
    val restaurantPhone by cartViewModel.restaurantPhone.collectAsState()

    // ── Estado para el Formulario de Pedido (BottomSheet) ───────
    val sheetState = rememberModalBottomSheetState()
    var showOrderSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${cartItems.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(80.dp), tint = TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("Tu carrito está vacío", color = TextMuted, fontSize = 18.sp)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(cartItems, key = { it.id }) { item ->
                        CartItemRow(item, cartViewModel)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow("Subtotal", "$${String.format("%,d", subtotal)}")
                        SummaryRow("Envío", "$3.500")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow("Total", "$${String.format("%,d", total)}", isTotal = true)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showOrderSheet = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceder al Pago", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // ✅ Integración del Formulario de Pedido
        if (showOrderSheet) {
            OrderFormBottomSheet(
                sheetState     = sheetState,
                cartViewModel  = cartViewModel,
                authViewModel  = authViewModel,
                orderViewModel = orderViewModel,
                onDismiss      = { showOrderSheet = false },
                onOrderPlaced  = { orderId ->
                    showOrderSheet = false
                    // Navegar a la confirmación con el ID generado
                    navController.navigate(Screen.OrderConfirmation.passId(orderId))
                }
            )
        }
    }
}

@Composable
fun CartItemRow(item: ReceiptItem, viewModel: CartViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl ?: item.imageRes,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.burguer)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                if (item.adiciones.isNotEmpty()) {
                    Text(
                        text = "Extras: " + item.adiciones.joinToString(", "),
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
                Text(
                    text = "$${String.format("%,d", item.price)} c/u",
                    color = RedPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.updateQuantity(item.id, false) }) {
                    Icon(Icons.Default.Remove, null, tint = TextMuted)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.updateQuantity(item.id, true) }) {
                    Icon(Icons.Default.Add, null, tint = RedPrimary)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = if (isTotal) RedPrimary else TextPrimary, fontWeight = FontWeight.Bold)
    }
}
