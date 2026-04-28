package com.example.appifood_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
@Composable
fun MinimalRestaurantCard(
name: String,
rating: String,
time: String,
imageRes: Int,
onClick: () -> Unit
) {
    // 1. Estado para controlar la animación
    var isClicked by remember { mutableStateOf(false) }

    // 2. Definición de las propiedades animadas
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.96f else 1f, // Escala: de 1.0 a 0.96 (pequeña reducción)
        animationSpec = tween(durationMillis = 150), // Duración rápida
        finishedListener = {
            // Cuando la animación de "hambre" termina, lanzamos la acción de navegación
            if (isClicked) {
                isClicked = false // Reseteamos para que vuelva a su estado original al volver
                onClick()
            }
        }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isClicked) 0.7f else 1f, // Opacidad: de 1.0 a 0.7 (ligero desvanecimiento)
        animationSpec = tween(durationMillis = 150)
    )

    val cardSize = 160.dp

    Card(
        modifier = Modifier
            .size(cardSize) // --- ESTO HACE QUE SEA UN CUADRADO PERFECTO ---
            .padding(8.dp)  // Padding uniforme
            .scale(scale)
            .alpha(alpha)
            .clickable { isClicked = true },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Recorta la imagen para llenar el cuadrado
            )

            // 2. Degradado para asegurar legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f))))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                // Título más pequeño y limpio
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Evita que el nombre largo se desborde
                )

                // Fila de información más compacta
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = " $rating",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • $time",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}