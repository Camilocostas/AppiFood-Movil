package com.example.appifood_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appifood_movil.R

@Composable
fun ImageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // 1. IMAGEN DE FONDO
        Image(
            painter = painterResource(id = R.drawable.hero_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. GRADIENTE (Desvanecido de Blanco a Transparente)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent,
                            Color.Transparent
                        ),
                        startY = Float.POSITIVE_INFINITY,
                        endY = 0f
                    )
                )
        )

        // 3. LOGO Y TEXTO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp), // Padding inferior con .dp
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logomau), // Cambia por el nombre de tu archivo de logo
                contentDescription = "Logo AppiFood",
                modifier = Modifier
                    .width(310.dp) // <--- Aumenta este valor para hacerlo más grande (antes era 220.dp)
                    .wrapContentHeight()
            )

            // CORRECCIÓN AQUÍ: Asegúrate de que top y horizontal usen .dp
            Text(
                text = "Descubre, ordena y disfruta la mejor comida",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = 4.dp, // <--- Reduce este valor (antes era 10.dp) para pegarlo al logo
                    start = 40.dp,
                    end = 40.dp
                )
            )
        }
    }
}
