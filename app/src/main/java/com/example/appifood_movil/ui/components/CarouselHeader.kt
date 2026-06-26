package com.example.appifood_movil.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appifood_movil.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselHeader(
    height: Dp = 280.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val images = listOf(
        R.drawable.flat_carousel,
        R.drawable.plate_carousel,
        R.drawable.top_carousel,
        R.drawable.helado
    )

    val virtualPageCount = Int.MAX_VALUE
    val initialPage = virtualPageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualPageCount }
    )

    // Auto-desplazamiento
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(durationMillis = 1200)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        // ── Carrusel de imágenes ──────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { virtualPage ->
            val actualImageIndex = virtualPage % images.size

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - virtualPage) + pagerState.currentPageOffsetFraction)
                        alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                        translationX = pageOffset * size.width
                    }
            ) {
                Image(
                    painter = painterResource(id = images[actualImageIndex]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay con gradiente para mejor legibilidad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0.5f,
                                endY = 1f
                            )
                        )
                )
            }
        }

        // ── Indicadores de página ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    val isActive = (pagerState.currentPage % images.size) == index
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isActive) YellowAccent
                                else Color.White.copy(alpha = 0.4f)
                            )
                            .graphicsLayer {
                                if (isActive) {
                                    // Animación sutil de escala
                                    alpha = 1f
                                } else {
                                    alpha = 0.6f
                                }
                            }
                    )
                }
            }
        }

        // ── Contenido personalizado ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = 65.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            content()
        }
    }
}