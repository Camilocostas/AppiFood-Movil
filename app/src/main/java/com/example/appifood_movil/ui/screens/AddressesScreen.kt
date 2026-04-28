package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
@Composable
fun AddressesScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Añadir nueva dirección +", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color.White)) {
            // Header igual al de BaseScreen
            Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
                Text("Mis direcciones", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
            }

            // Simulación de mapa con un estilo minimalista
            Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(20.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5))) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.align(Alignment.Center).size(48.dp), tint = Color(0xFFFF4B3A))
            }

            // Lista de ubicaciones
            Column(modifier = Modifier.padding(20.dp)) {
                listOf("Casa" to "Calle 10 #5-20", "Trabajo" to "Carrera 9 #12-45").forEach { (titulo, dir) ->
                    Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, null, tint = Color.Gray)
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(titulo, fontWeight = FontWeight.Bold)
                            Text(dir, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Divider(color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}