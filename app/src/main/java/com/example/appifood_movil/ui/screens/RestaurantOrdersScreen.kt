// ui/screens/RestaurantOrdersScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp        // ✅ Para LazyColumn
import androidx.compose.foundation.lazy.items              // ✅ Para items en LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.RestaurantOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantOrdersScreen(
    navController: NavController,
    viewModel: RestaurantOrderViewModel = hiltViewModel()
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showStatusFilter by remember { mutableStateOf(false) }
    val statusOptions = listOf("Todos", "pending", "preparing", "ready", "delivered", "cancelled")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 Pedidos",
                            fontWeight = FontWeight.Bold
                        )
                        if (pendingCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFFD32F2F),
                                modifier = Modifier
                            ) {
                                Text(
                                    text = "$pendingCount",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showStatusFilter = !showStatusFilter }
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            tint = if (selectedStatus != null) Color(0xFFD32F2F) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros rápidos
            if (showStatusFilter) {
                StatusFilterChips(
                    statusOptions = statusOptions,
                    selectedStatus = selectedStatus,
                    onStatusSelected = { status ->
                        viewModel.filterByStatus(if (status == "Todos") null else status)
                        showStatusFilter = false
                    }
                )
            }

            // Lista de pedidos
            if (orders.isEmpty()) {
                EmptyOrdersView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            onClick = {
                                navController.navigate(
                                    Screen.RestaurantOrderDetail.passOrderId(order.orderId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterChips(
    statusOptions: List<String>,
    selectedStatus: String?,
    onStatusSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(statusOptions) { status ->
            val isSelected = if (status == "Todos") {
                selectedStatus == null
            } else {
                selectedStatus == status
            }

            FilterChip(
                selected = isSelected,
                onClick = { onStatusSelected(status) },
                label = {
                    Text(
                        text = when (status) {
                            "Todos" -> "Todos"
                            "pending" -> "📌 Pendientes"
                            "preparing" -> "👨‍🍳 Preparando"
                            "ready" -> "✅ Listos"
                            "delivered" -> "📦 Entregados"
                            "cancelled" -> "❌ Cancelados"
                            else -> status
                        },
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFD32F2F),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: ID y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cliente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👤 ${order.customer.fullName}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "📱 ${order.customer.phone}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dirección de entrega
            Text(
                text = "📍 ${order.deliveryAddress}",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Productos
            Column {
                order.items.take(2).forEach { item ->
                    Text(
                        text = "• ${item.quantity}x ${item.name}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
                if (order.items.size > 2) {
                    Text(
                        text = "+ ${order.items.size - 2} más",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Total y método de pago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = "💳 ${order.payment.method}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Total: ",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${String.format("%,d", order.total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
                Text(
                    text = formatTime(order.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, label) = when (status) {
        "pending" -> Color(0xFFFF9800) to "Pendiente"
        "preparing" -> Color(0xFF2196F3) to "Preparando"
        "ready" -> Color(0xFF4CAF50) to "Listo"
        "delivered" -> Color(0xFF9E9E9E) to "Entregado"
        "cancelled" -> Color(0xFFD32F2F) to "Cancelado"
        else -> Color.Gray to status
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyOrdersView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📦",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay pedidos",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Los pedidos aparecerán aquí cuando lleguen",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}