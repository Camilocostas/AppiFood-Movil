package com.example.appifood_movil.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.appifood_movil.R

// ── Paleta AppiFood ───────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val WhiteFull    = Color.White
private val WhiteSoft    = Color.White.copy(alpha = 0.85f)
private val WhiteMuted   = Color.White.copy(alpha = 0.55f)

data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int,
    val tag: String = "",
    val backgroundColor: Color = RedPrimary
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {

    val pages = listOf(
        OnboardingPage(
            title       = "¡ANTOJO CUMPLIDO!",
            description = "Explora los mejores sabores de Popayán desde la palma de tu mano. Rápido, caliente y delicioso.",
            image       = R.drawable.burguer,
            tag         = "🍔 Pide en segundos"
        ),
        OnboardingPage(
            title       = "¡ENCUENTRA TU RESTAURANTE MÁS CERCANO!",
            description = "Encuentra tu restaurante favorito en Popayán y reserva tu mesa en segundos.",
            image       = R.drawable.restaurante,
            tag         = "📍 Cerca de ti"
        ),
        OnboardingPage(
            title       = "¿LISTO PARA EMPEZAR?",
            description = "Crea tu cuenta ahora y disfruta de ofertas exclusivas en tus restaurantes favoritos.",
            image       = R.drawable.logomau,
            tag         = "🎁 Ofertas exclusivas"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()
    val isLast     = pagerState.currentPage == pages.size - 1

    // ── Estado de animación de entrada ───────────────────────────
    // `visible` arranca en false; se activa en el primer frame
    // para disparar la transición de entrada sin bloquear el render.
    var visible by remember { mutableStateOf(false) }

    // Opacidad global: 0f → 1f en 500ms con EaseOut
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label         = "screenAlpha"
    )

    // Traslación vertical de la imagen: entra deslizándose desde +40dp → 0
    val imageOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label         = "imageOffsetY"
    )

    // Traslación vertical del bloque de texto: entra desde +30dp → 0
    // Con delay de 100ms para que salga ligeramente después de la imagen
    // (entrada escalonada = sensación más premium)
    val textOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 30.dp,
        animationSpec = tween(
            durationMillis = 550,
            delayMillis    = 100,
            easing         = FastOutSlowInEasing
        ),
        label = "textOffsetY"
    )

    // Traslación de la barra inferior: entra desde +20dp → 0, delay mayor
    val bottomBarOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 20.dp,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis    = 180,
            easing         = FastOutSlowInEasing
        ),
        label = "bottomBarOffsetY"
    )

    // Dispara la animación en el primer frame de composición
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RedPrimary, RedDark, RedDeep)
                )
            )
            // Fade-in global de toda la pantalla
            .graphicsLayer { alpha = screenAlpha }
    ) {
        DecorativeCircles()

        Column(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f)
            ) { position ->
                OnboardingPageUI(
                    page          = pages[position],
                    imageOffsetY  = imageOffsetY,
                    textOffsetY   = textOffsetY
                )
            }

            // Barra inferior con su propia traslación de entrada
            Box(
                modifier = Modifier.offset(y = bottomBarOffsetY)
            ) {
                BottomBar(
                    pageCount   = pages.size,
                    currentPage = pagerState.currentPage,
                    isLast      = isLast,
                    onNext      = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onFinished  = onFinished
                )
            }
        }
    }
}

// ── Círculos decorativos ──────────────────────────────────────────
@Composable
fun DecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
    }
}

// ── Página individual ─────────────────────────────────────────────
@Composable
fun OnboardingPageUI(
    page         : OnboardingPage,
    imageOffsetY : androidx.compose.ui.unit.Dp,
    textOffsetY  : androidx.compose.ui.unit.Dp
) {
    // Float animado de la imagen (loop continuo, independiente de la entrada)
    val infiniteTransition = rememberInfiniteTransition(label = "img_float")
    val imgScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.04f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "img_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        // ── Tag pill con entrada propia ───────────────────────────
        if (page.tag.isNotEmpty()) {
            Surface(
                shape  = RoundedCornerShape(50),
                color  = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(y = imageOffsetY)   // sube con la imagen
            ) {
                Text(
                    text       = page.tag,
                    color      = WhiteFull,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        }

        // ── Imagen con slide desde abajo ──────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .offset(y = imageOffsetY)   // <-- traslación de entrada
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Image(
                painter      = painterResource(id = page.image),
                contentDescription = null,
                modifier     = Modifier
                    .size(280.dp)
                    .scale(imgScale),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Bloque de texto con slide desde abajo (delay 100ms) ───
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = textOffsetY)   // <-- traslación de texto
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(YellowAccent)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = page.title,
                fontSize   = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = WhiteFull,
                textAlign  = TextAlign.Center,
                lineHeight = 32.sp,
                modifier   = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text       = page.description,
                color      = WhiteSoft,
                textAlign  = TextAlign.Center,
                fontSize   = 15.sp,
                lineHeight = 23.sp,
                modifier   = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

// ── Barra inferior ────────────────────────────────────────────────
@Composable
fun BottomBar(
    pageCount   : Int,
    currentPage : Int,
    isLast      : Boolean,
    onNext      : () -> Unit,
    onFinished  : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(bottom = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                val isActive = currentPage == index
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (isActive) 28.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isActive) YellowAccent else WhiteMuted)
                )
            }
        }

        if (isLast) {
            Button(
                onClick        = onFinished,
                shape          = RoundedCornerShape(50),
                colors         = ButtonDefaults.buttonColors(
                    containerColor = YellowAccent,
                    contentColor   = Color(0xFF1A0000)
                ),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                elevation      = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text       = "¡Comenzar!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 15.sp
                )
            }
        } else {
            IconButton(
                onClick  = onNext,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(YellowAccent)
            ) {
                Icon(
                    imageVector        = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Siguiente",
                    tint               = Color(0xFF1A0000),
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
    }
}