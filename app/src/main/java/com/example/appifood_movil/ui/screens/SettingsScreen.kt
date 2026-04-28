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
import com.example.appifood_movil.ui.components.BaseScreen

@Composable
fun SettingsScreen(navController: NavController) {
    BaseScreen("Configuración", navController) {
        // Sección Cuenta
        ProfileSectionCard("Cuenta") {
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Notificaciones", modifier = Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = {})
            }
        }

        // Sección Privacidad
        ProfileSectionCard("Privacidad") {
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Perfil privado", modifier = Modifier.weight(1f))
                Switch(checked = false, onCheckedChange = {})
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A))
        ) {
            Text("Actualizar datos")
        }
    }
}