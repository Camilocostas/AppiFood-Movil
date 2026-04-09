package com.example.appifood_movil.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.R // Asegúrate de que este import sea correcto para tu proyecto

// 1. Modelo de Datos Actualizado
data class Pedido(
    val idPedido: String,
    val nombreProducto: String,
    val descripcion: String, // Breve descripción (ej: "Con papas fritas y bebida")
    val restaurante: String,
    val imagenRes: Int, // ID de la imagen en drawable
    val precioTotal: Double,
    val fecha: String,
    val tiempoEstimadoMin: Int,
    val estado: EstadoPedido, // Usamos un Enum para mayor control
    val metodoPago: MetodoPago // Usamos una Sealed Class para datos de pago
)

enum class EstadoPedido(val texto: String, val color: Color) {
    EN_PREPARACION("En preparación", Color(0xFFFF9800)), // Naranja
    EN_CAMINO("En camino", Color(0xFF2196F3)), // Azul
    ENTREGADO("Entregado", Color(0xFF4CAF50)), // Verde
    CANCELADO("Cancelado", Color(0xFFF44336)) // Rojo
}

sealed class MetodoPago(val tipo: String, val icono: ImageVector) {
    class Efectivo(val montoAPagar: Double) : MetodoPago("Efectivo", Icons.Default.Payments)
    class Transferencia(val plataforma: String, val numCuenta: String) : MetodoPago("Transferencia", Icons.Default.AccountBalanceWallet)
}

// 2. Datos de Prueba Ampliados
val listaPedidosPrueba = listOf(
    Pedido(
        idPedido = "AF-1024",
        nombreProducto = "Cheeseburger Doble",
        descripcion = "Carne Angus, doble queso cheddar, cebolla caramelizada.",
        restaurante = "Burger Masters",
        imagenRes = R.drawable.cheese,
        precioTotal = 25000.0,
        fecha = "Hoy, 2:30 PM",
        tiempoEstimadoMin = 25,
        estado = EstadoPedido.EN_CAMINO,
        metodoPago = MetodoPago.Transferencia("Nequi", "3154567890")
    ),
    Pedido(
        idPedido = "AF-1023",
        nombreProducto = "Pizza Familiar Pepperoni",
        descripcion = "Masa artesanal, salsa pomodoro, mozzarella.",
        restaurante = "Bella Pizza",
        imagenRes = R.drawable.pizza  , // Reemplaza por imagen de pizza si tienes
        precioTotal = 45000.0,
        fecha = "Hoy, 1:15 PM",
        tiempoEstimadoMin = 35,
        estado = EstadoPedido.EN_PREPARACION,
        metodoPago = MetodoPago.Efectivo(45000.0)
    ),
    Pedido(
        idPedido = "AF-1020",
        nombreProducto = "Ramen Tonkotsu Pork",
        descripcion = "Caldo de cerdo, fideos frescos, huevo ajitama.",
        restaurante = "Ichiraku Ramen",
        imagenRes = R.drawable.ramen,
        precioTotal = 32000.0,
        fecha = "Ayer",
        tiempoEstimadoMin = 0,
        estado = EstadoPedido.ENTREGADO,
        metodoPago = MetodoPago.Transferencia("Paypal", "mauricio.b@mail.com")
    ),
    Pedido(
        idPedido = "AF-1018",
        nombreProducto = "Coca Cola 1.5L",
        descripcion = "Bebida gaseosa original.",
        restaurante = "Tienda Local",
        imagenRes = R.drawable.cocacola, // Reemplaza por imagen de bebida
        precioTotal = 7500.0,
        fecha = "Ayer",
        tiempoEstimadoMin = 0,
        estado = EstadoPedido.ENTREGADO,
        metodoPago = MetodoPago.Efectivo(7500.0)
    ),
    Pedido(
        idPedido = "AF-1015",
        nombreProducto = "Combo Philadelphia Sushi",
        descripcion = "12 piezas de sushi roll con salmón y queso crema.",
        restaurante = "Sushi Hana",
        imagenRes = R.drawable.philadelphia,
        precioTotal = 38000.0,
        fecha = "Hace 2 días",
        tiempoEstimadoMin = 0,
        estado = EstadoPedido.ENTREGADO,
        metodoPago = MetodoPago.Transferencia("Bancolombia", "Ahorros 456-123456-78")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    // Estado para la pestaña seleccionada (0: En entrega, 1: Finalizados)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("En entrega", "Finalizados")

    // Filtrar pedidos según el estado
    val pedidosEnEntrega = listaPedidosPrueba.filter { it.estado == EstadoPedido.EN_PREPARACION || it.estado == EstadoPedido.EN_CAMINO }
    val pedidosFinalizados = listaPedidosPrueba.filter { it.estado == EstadoPedido.ENTREGADO || it.estado == EstadoPedido.CANCELADO }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de pedidos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F7F7) // Fondo gris muy claro para contraste
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pestañas (Tabs) de selección
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFFFF4B3A), // Color acento AppiFood
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
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    )
                }
            }

            // Contenido de la pestaña
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val pedidosAMostrar = if (selectedTabIndex == 0) pedidosEnEntrega else pedidosFinalizados

                if (pedidosAMostrar.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay pedidos para mostrar", color = Color.Gray)
                        }
                    }
                } else {
                    items(pedidosAMostrar) { pedido ->
                        OrderHistoryCard(pedido = pedido)
                    }
                }
            }
        }
    }
}

// 3. Composable de la Tarjeta de Pedido (Inspirado en tu ejemplo)
@Composable
fun OrderHistoryCard(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila Superior: Imagen y Detalles Principales
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = pedido.imagenRes),
                    contentDescription = pedido.nombreProducto,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pedido.nombreProducto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = pedido.descripcion,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Restaurante y Fecha
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(text = " ${pedido.restaurante}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "• ${pedido.fecha}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Separador sutil
            Divider(color = Color(0xFFF1F1F1), modifier = Modifier.padding(vertical = 12.dp))

            // Fila Inferior: Info Adicional (Tiempo/Estado y Pago)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info de Tiempo o Estado
                if (pedido.estado == EstadoPedido.EN_PREPARACION || pedido.estado == EstadoPedido.EN_CAMINO) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                        Text(
                            text = " Llega en ~${pedido.tiempoEstimadoMin} mins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF9800)
                        )
                    }
                } else {
                    // Estado para finalizados
                    Text(
                        text = pedido.estado.texto,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = pedido.estado.color,
                        modifier = Modifier
                            .background(pedido.estado.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Info de Pago
                PaymentInfoSection(
                    metodoPago = pedido.metodoPago,
                    total = pedido.precioTotal,
                    // Agregamos esta línea para que sepa si debe ponerse verde:
                    estaPagado = pedido.estado == EstadoPedido.ENTREGADO
                )
            }
        }
    }
}

// 4. Composable para la sección de pago dinámica
@Composable
fun PaymentInfoSection(metodoPago: MetodoPago, total: Double, estaPagado: Boolean = false) {
    val totalFormateado = "$ ${String.format("%,.0f", total)}"

    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = metodoPago.icono,
                contentDescription = null,
                // Si está pagado, el icono se pone verde
                tint = if (estaPagado || metodoPago is MetodoPago.Transferencia) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = " ${metodoPago.tipo}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (estaPagado || metodoPago is MetodoPago.Transferencia) Color(0xFF4CAF50) else Color.Black
            )
        }

        when (metodoPago) {
            is MetodoPago.Transferencia -> {
                Text(text = totalFormateado, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                Text(text = "${metodoPago.plataforma}: ${metodoPago.numCuenta}", fontSize = 11.sp, color = Color.Gray)
                Text(text = "Pagado", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            }
            is MetodoPago.Efectivo -> {
                // Si el pedido ya se entregó o marcaste como pagado, sale verde
                val colorEstado = if (estaPagado) Color(0xFF4CAF50) else Color(0xFFFF4B3A)
                val textoEstado = if (estaPagado) "Pagado" else "Pendiente de pago"

                Text(
                    text = totalFormateado,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )
                Text(
                    text = textoEstado,
                    fontSize = 11.sp,
                    color = colorEstado,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}