package com.example.appifood_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Asegúrate de tener este import
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.appifood_movil.navigation.AppNavigation
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Manejar la Splash Screen antes que nada
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // 2. Habilitar el modo de pantalla completa (Edge-to-Edge)
        enableEdgeToEdge()

        setContent {
            // Usa el nombre exacto de tu tema (el que genera Android Studio por defecto)
            AppifoodMovilTheme {
                // Punto de entrada de la navegación
                AppNavigation()
            }
        }
    }
}