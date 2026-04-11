package com.example.appifood_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appifood_movil.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppiFoodHeader(
    searchText: String,
    onSearchChange: (String) -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)
    val searchBarBackground = Color(0xFFF2F2F2).copy(alpha = 0.9f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(appiFoodRed)
            // 1. Añadimos statusBarsPadding para que no choque con la hora
            .statusBarsPadding()
            // 2. Ajustamos el padding superior original de 25.dp a uno más pequeño
            // porque statusBarsPadding ya hace gran parte del trabajo.
            .padding(top = 25.dp, start = 20.dp, end = 20.dp, bottom = 25.dp)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_appifood),
            contentDescription = "AppiFood",
            modifier = Modifier.height(35.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ubicación
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Popayán, Cauca", fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Saludo
        Text("Hola, Camilo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(modifier = Modifier.height(25.dp))

        // Buscador
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp)),
            placeholder = { Text("¿Deseas algo en especial?", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = searchBarBackground,
                unfocusedContainerColor = searchBarBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}