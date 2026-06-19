package com.example.appifood_movil.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.appifood_movil.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    val appiFoodRed = Color(0xFFD32F2F)

    // Cargar la animación
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.logo)
    )

    // Animar la composición
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true,
        speed = 1f
    )

    // Desvanecer al final
    val fadeOut by animateFloatAsState(
        targetValue = if (progress >= 0.9f) 0f else 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "fadeOut"
    )

    // Ir a la siguiente pantalla cuando termine
    LaunchedEffect(progress) {
        if (progress >= 1f) {
            delay(1000)
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appiFoodRed)
            .graphicsLayer { alpha = fadeOut },
        contentAlignment = Alignment.Center
    ) {

        if (composition == null) {
            Text("")
        } else {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxSize(0.9f), // 90% de la pantalla
                alignment = Alignment.Center
            )
        }
    }
}