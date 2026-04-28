package com.example.appifood_movil.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.compose.foundation.shape.CircleShape

@Composable
fun SwipeActionButton(
    onAddToCart: () -> Unit
) {
    val naranjaPrincipal = Color(0xFFFF4B3A)
    val naranjaClaro = Color(0xFFFF8C7F)

    val swipeLimit = 220f
    var offsetX by remember { mutableStateOf(0f) }
    var actionTriggered by remember { mutableStateOf(false) }

    val offsetAnim = animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "SwipeOffsetAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFFF5F5F5)) // Fondo gris muy claro para el track
            .padding(4.dp)
    ) {
        // --- FONDO ROJO GRADIENTE ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(naranjaPrincipal, naranjaClaro)
                    )
                )
                .alpha((offsetX / swipeLimit).coerceIn(0.2f, 1f)) // Se intensifica al deslizar
        )

        // --- TEXTO ---
        Text(
            text = if (actionTriggered) "Agregado!" else "Desliza para agregar al carrito →",
            fontSize = 14.sp,
            color = if (actionTriggered) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        // --- BOTÓN DESLIZABLE ---
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterStart)
                .offset { IntOffset(offsetAnim.value.roundToInt(), 0) }
                .clip(CircleShape)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            // Permitimos deslizar solo hacia la derecha (0 a swipeLimit)
                            offsetX = (offsetX + dragAmount).coerceIn(0f, swipeLimit)
                        },
                        onDragEnd = {
                            if (offsetX >= swipeLimit * 0.8f) {
                                actionTriggered = true
                                onAddToCart()
                            } else {
                                offsetX = 0f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Carrito",
                tint = naranjaPrincipal,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}