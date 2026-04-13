package com.example.appifood_movil.ui.screens

import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ProfileScreen(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)
    val lightGray = Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // --- CABECERA ROJA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(appiFoodRed),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. ESPACIO PARA BAJAR EL CONTENIDO
                Spacer(modifier = Modifier.height(80.dp))

                // 2. Imagen de Perfil (Círculo con inicial)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8A80)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Nombre y Correo
                Text(
                    text = "Camilo Acosta",
                    color = Color.White,
                    fontSize = 22.sp, // Lo subí un poquito de tamaño para que resalte más
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "camilo@gmail.com",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // --- SECCIÓN: INFORMACIÓN PERSONAL ---
        ProfileSectionCard(title = "Información personal") {
            InfoRow(label = "Nombre", value = "Camilo Acosta")
            InfoRow(label = "Celular", value = "312 345 6789")
            InfoRow(label = "Correo", value = "camilo@gmail.com")
            InfoRow(label = "Género", value = "Masculino", isLast = true)
        }

        // --- SECCIÓN: MI CUENTA ---
        ProfileSectionCard(title = "Mi cuenta") {
            MenuRow(icon = Icons.Default.List, title = "Mis pedidos", badgeCount = 3)
            MenuRow(
                icon = Icons.Default.FavoriteBorder,
                title = "Favoritos",
                onClick = {
                    navController.navigate("favorites")
                }
            )
            MenuRow(icon = Icons.Default.LocationOn, title = "Mis direcciones")
            MenuRow(icon = Icons.Default.CreditCard, title = "Métodos de pago")
            MenuRow(icon = Icons.Default.Notifications, title = "Notificaciones")
            MenuRow(icon = Icons.Default.HeadsetMic, title = "Centro de ayuda", isLast = true)
        }

        // --- BOTÓN CERRAR SESIÓN ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // 1. Navega a la pantalla de login (ajusta "login" al nombre de tu ruta)
                    navController.navigate("auth") {
                        // 2. Limpia el historial para que no pueda volver atrás al perfil
                        popUpTo("home") { inclusive = true }
                    }
                }
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = appiFoodRed,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(appiFoodRed.copy(alpha = 0.1f))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Cerrar sesión",
                color = appiFoodRed,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el BottomBar
    }
}

@Composable
fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.Gray, fontSize = 14.sp)
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        if (!isLast) Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
    }
}

@Composable
fun MenuRow(icon: ImageVector, onClick: () -> Unit = {}, title: String, badgeCount: Int = 0, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() } // Hacemos que toda la fila sea clickeable
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF4B3A),
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFF4B3A).copy(alpha = 0.1f))
                    .padding(6.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp)

            if (badgeCount > 0) {
                Surface(
                    color = Color.Red,
                    shape = CircleShape,
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = badgeCount.toString(), color = Color.White, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
        if (!isLast) Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
    }
}