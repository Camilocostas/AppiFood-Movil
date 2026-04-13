package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale

// Importa el R de tu proyecto para las imágenes
import com.example.appifood_movil.R

// 1. MODELO DE DATOS
data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int,
    val backgroundColor: Color = Color(0xFFD32F2F)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage("¡ANTOJO CUMPLIDO!", "Explora los mejores sabores de Popayán desde la palma de tu mano. Rápido, caliente y delicioso.", R.drawable.burguer), // Cambia por tus nombres
        OnboardingPage("¡ENCUENTRA TU RESTAURANTE MAS CERCANO!", "Encuentra tu restaurante de preferencia en popayán y reserva tu mesa en segundos.", R.drawable.restaurante),
        OnboardingPage("¿LISTO PARA EMPEZAR?", "Crea tu cuenta ahora y disfruta de ofertas exclusivas en tus restaurantes favoritos.", R.drawable.logomau)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pages[pagerState.currentPage].backgroundColor)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { position ->
            OnboardingPageUI(pages[position])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                repeat(pages.size) { it ->
                    val color = if (pagerState.currentPage == it) Color.Yellow else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(10.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Yellow)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Cambiamos de Center a Top para controlar nosotros los espacios
    ) {
        // 1. Espacio flexible arriba para que la imagen no toque el borde
        Spacer(modifier = Modifier.height(60.dp))

        // 2. La Imagen (Ajustamos el tamaño si es necesario para que suba más)
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier
                .size(380.dp) // Un poco más grande para llenar el espacio superior
                .padding(40.dp),
            contentScale = ContentScale.Fit
        )

        // 3. Este Spacer empuja las letras hacia ABAJO
        Spacer(modifier = Modifier.weight(1f))

        // 4. Bloque de Textos (Ahora quedarán más abajo gracias al weight de arriba)
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 30.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(bottom = 60.dp), // El padding bottom asegura que no choquen con los botones
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
    }
}