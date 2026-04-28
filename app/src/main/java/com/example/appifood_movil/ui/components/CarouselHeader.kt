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

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(height)) {
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