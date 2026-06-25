// MainActivity.kt
package com.example.appifood_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appifood_movil.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // AppifoodMovilTheme ya maneja el estado del tema internamente
            // y provee LocalThemeState para que cualquier pantalla
            // pueda leer isDarkMode o llamar toggle()
            AppifoodMovilTheme {
                val searchViewModel: SearchViewModel = hiltViewModel()
                AppNavigation(searchViewModel = searchViewModel)
            }
        }
    }
}