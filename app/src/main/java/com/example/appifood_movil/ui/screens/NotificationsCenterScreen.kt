package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsCenterScreen(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Centro de notificaciones", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appiFoodRed)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Notificación
            NotificationItem(
                icon = Icons.Default.DeliveryDining,
                title = "¡Tu pedido está en camino!",
                description = "Tu pedido de Cheeseburger llegará en 15 minutos",
                time = "Hace 5 min",
                isRead = false
            )

            NotificationItem(
                icon = Icons.Default.Star,
                title = "¡Nueva oferta disponible!",
                description = "20% de descuento en tu próximo pedido",
                time = "Hace 2 horas",
                isRead = false
            )

            NotificationItem(
                icon = Icons.Default.CheckCircle,
                title = "Pedido entregado",
                description = "Tu pedido de Big Mac fue entregado exitosamente",
                time = "Ayer",
                isRead = true
            )

            NotificationItem(
                icon = Icons.Default.Person,
                title = "Bienvenido a AppFood",
                description = "Completa tu perfil para mejores recomendaciones",
                time = "2 días atrás",
                isRead = true
            )
        }
    }
}

@Composable
fun NotificationItem(
    icon: ImageVector,
    title: String,
    description: String,
    time: String,
    isRead: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) Color.White else Color(0xFFFFF0EE)
        ),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            // Círculo con icono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFF4B3A).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFFF4B3A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = time,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}