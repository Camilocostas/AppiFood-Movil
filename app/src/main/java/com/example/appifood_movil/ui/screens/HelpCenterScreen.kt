package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpCenterScreen(navController: NavController) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Encabezado
        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
            Text("Centro de ayuda", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
        }

        // Animación de entrada para el contenido
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 100 })
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Grid de opciones (2x2)
                val items = listOf(
                    Triple("Preguntas Frecuentes", Icons.Default.QuestionMark, Color(0xFF4B8BFF)),
                    Triple("Chat en vivo", Icons.Default.Chat, Color(0xFF4CAF50)),
                    Triple("Enviar ticket", Icons.Default.Email, Color(0xFFFF9800)),
                    Triple("Teléfonos", Icons.Default.HeadsetMic, Color(0xFFFF4B3A))
                )

                for (i in items.indices step 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        HelpOptionCard(items[i], Modifier.weight(1f))
                        if (i + 1 < items.size) HelpOptionCard(items[i+1], Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de voz (Te escuchamos)
                Card(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                ) {
                    Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, null, tint = Color(0xFFFF4B3A))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Te escuchamos: Envíanos un mensaje de voz", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun HelpOptionCard(item: Triple<String, ImageVector, Color>, modifier: Modifier) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(item.second, null, tint = item.third, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.first, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
        }
    }
}