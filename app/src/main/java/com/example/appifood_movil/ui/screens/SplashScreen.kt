package com.example.appifood_movil.ui.screens

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.appifood_movil.data.api.RetrofitClient
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.appifood_movil.R
import kotlinx.coroutines.delay

// ── Paleta AppiFood ─────────────────────────────────────────────
private val RedPrimary = Color(0xFFD32F2F)
private val RedDark    = Color(0xFFB71C1C)
private val RedDeep    = Color(0xFF7F0000)

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── Estado de la animación Lottie ──────────────────────────
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.logo)
    )

    // Controlar manualmente la reproducción
    var isPlaying by remember { mutableStateOf(true) }
    var animationProgress by remember { mutableStateOf(0f) }

    // ── ANIMACIÓN LOTTIE CON CONTROL MANUAL ────────────────────
    // Prueba rápida — puedes ponerla temporalmente en SplashScreen
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.healthCheck()
            if (response.isSuccessful && response.body()?.status == "ok") {
                Log.d("API", "✅ Railway respondió correctamente")
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Railway no responde: ${e.message}")
            // Railway tiene cold start de ~3 min si estuvo inactivo
        }
    }

    // ── Fade out al final ──────────────────────────────────────
    val fadeOut by animateFloatAsState(
        targetValue = if (animationProgress >= 0.9f) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "fadeOut"
    )

    // ── FALLBACK: Si la animación no se reproduce, navegar después de 5 segundos ──
    LaunchedEffect(composition) {
        if (composition != null) {
            // Reproducir la animación completa
            while (isPlaying && animationProgress < 1f) {
                delay(16) // ~60fps
                animationProgress += 0.01f // Velocidad de reproducción
                if (animationProgress >= 1f) {
                    animationProgress = 1f
                    isPlaying = false
                    // Esperar un momento y navegar
                    delay(500)
                    onFinished()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RedPrimary, RedDark, RedDeep)
                )
            )
            .graphicsLayer { alpha = fadeOut }
            // ── TOCAR PARA SALTAR (SOLO EN CASO DE EMERGENCIA) ──
            .clickable {
                if (animationProgress < 1f) {
                    // Si tocas la pantalla, saltar la animación
                    onFinished()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // ── Círculos decorativos ────────────────────────────────
        SplashDecorativeCircles()

        // ── Animación Lottie ─────────────────────────────────────
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { animationProgress },
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .graphicsLayer { /* z elevado sobre los círculos */ },
                alignment = Alignment.Center
            )
        } else {
            // Fallback: mostrar texto si Lottie no carga
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )
            }
        }
    }
}

// ── Círculos decorativos de fondo ────────────────────────────────
@Composable
fun SplashDecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-100).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.03f))
        )
    }
}