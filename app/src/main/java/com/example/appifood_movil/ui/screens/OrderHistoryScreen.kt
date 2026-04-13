package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.R

// --- Models ---
data class Order(
    val orderId: String,
    val productName: String,
    val description: String,
    val restaurant: String,
    val imageRes: Int,
    val totalPrice: Double,
    val date: String,
    val estimatedTimeMin: Int,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod
)

enum class OrderStatus(val label: String, val color: Color) {
    PREPARING("En preparación", Color(0xFFFF9800)),
    ON_THE_WAY("En camino", Color(0xFF2196F3)),
    DELIVERED("Entregado", Color(0xFF4CAF50)),
    CANCELLED("Cancelado", Color(0xFFF44336))
}

sealed class PaymentMethod(val type: String, val icon: ImageVector) {
    class Cash(val amountToPay: Double) : PaymentMethod("Efectivo", Icons.Default.Payments)
    class Transfer(val platform: String, val accountNumber: String) : PaymentMethod("Transferencia", Icons.Default.AccountBalanceWallet)
}

// --- Mock Data ---
val mockOrderList = listOf(
    Order(
        orderId = "AF-1024",
        productName = "Cheeseburger Doble",
        description = "Carne Angus, doble queso cheddar, cebolla caramelizada.",
        restaurant = "Burger Masters",
        imageRes = R.drawable.cheese,
        totalPrice = 25000.0,
        date = "Hoy, 2:30 PM",
        estimatedTimeMin = 25,
        status = OrderStatus.ON_THE_WAY,
        paymentMethod = PaymentMethod.Transfer("Nequi", "3154567890")
    ),
    Order(
        orderId = "AF-1020",
        productName = "Ramen Tonkotsu Pork",
        description = "Caldo de cerdo, fideos frescos, huevo ajitama.",
        restaurant = "Ichiraku Ramen",
        imageRes = R.drawable.ramen,
        totalPrice = 32000.0,
        date = "Ayer",
        estimatedTimeMin = 0,
        status = OrderStatus.DELIVERED,
        paymentMethod = PaymentMethod.Transfer("Paypal", "mauricio.b@mail.com")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("En entrega", "Finalizados")

    val ongoingOrders = mockOrderList.filter { it.status == OrderStatus.PREPARING || it.status == OrderStatus.ON_THE_WAY }
    val completedOrders = mockOrderList.filter { it.status == OrderStatus.DELIVERED || it.status == OrderStatus.CANCELLED }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = Color(0xFFFF4B3A),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFFFF4B3A)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val ordersToShow = if (selectedTabIndex == 0) ongoingOrders else completedOrders

                if (ordersToShow.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay pedidos para mostrar", color = Color.Gray)
                        }
                    }
                } else {
                    items(ordersToShow) { order ->
                        OrderHistoryCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = order.imageRes),
                    contentDescription = order.productName,
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = order.productName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(text = order.description, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restaurant, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(text = " ${order.restaurant}", fontSize = 12.sp, color = Color.Gray)
                        Text(text = " • ${order.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F1F1))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (order.status == OrderStatus.PREPARING || order.status == OrderStatus.ON_THE_WAY) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                        Text(" Llega en ~${order.estimatedTimeMin} mins", color = Color(0xFFFF9800), fontSize = 13.sp)
                    }
                } else {
                    Text(
                        text = order.status.label,
                        color = order.status.color,
                        modifier = Modifier.background(order.status.color.copy(0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                PaymentInfoSection(method = order.paymentMethod, total = order.totalPrice, isPaid = order.status == OrderStatus.DELIVERED)
            }
        }
    }
}

@Composable
fun PaymentInfoSection(method: PaymentMethod, total: Double, isPaid: Boolean) {
    val formattedTotal = "$ ${String.format("%,.0f", total)}"
    val successColor = Color(0xFF4CAF50)

    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(method.icon, null, tint = if (isPaid) successColor else Color.Gray, modifier = Modifier.size(16.dp))
            Text(" ${method.type}", fontWeight = FontWeight.Bold, color = if (isPaid) successColor else Color.Black)
        }
        Text(text = formattedTotal, fontWeight = FontWeight.Bold, color = if (isPaid) successColor else Color(0xFFFF4B3A))
        Text(text = if (isPaid) "Pagado" else "Pendiente", fontSize = 11.sp, color = if (isPaid) successColor else Color.Gray)
    }
}