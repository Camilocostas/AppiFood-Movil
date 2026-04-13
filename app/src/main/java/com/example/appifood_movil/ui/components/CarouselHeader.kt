package com.example.appifood_movil.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appifood_movil.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import androidx.compose.animation.core.tween

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselHeader(
    height: Dp = 280.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val images = listOf(
        R.drawable.pizza,
        R.drawable.ojotigre, // Cambia por tus imágenes reales
        R.drawable.cheese,
        R.drawable.hero_login
    )

    val virtualPageCount = Int.MAX_VALUE
    val initialPage = virtualPageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualPageCount }
    )

    // Lógica Automática (Aquí está el secreto)
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            // Usamos animateScrollToPage para que el valor de offset cambie suavemente
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(durationMillis = 1000) // Transición de segundo y medio
            )
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(height)) {
        // HorizontalPager con el efecto de desvanecido (graphicsLayer)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { virtualPage ->
            val actualImageIndex = virtualPage % images.size
            Image(
                painter = painterResource(id = images[actualImageIndex]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - virtualPage) + pagerState.currentPageOffsetFraction)
                        alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                        translationX = pageOffset * size.width
                    }
            )
        }

        // Gradiente blanco
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.Transparent, Color.Transparent),
                        startY = Float.POSITIVE_INFINITY,
                        endY = 0f
                    )
                )
        )

        // CONTENIDO: Logo, Ubicación y Saludo
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Bajamos el bottom de 40.dp a 60.dp (o más) para que los elementos suban
                .padding(start = 24.dp, end = 24.dp, bottom = 65.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            content()
        }
    }
}