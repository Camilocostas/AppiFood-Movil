package com.example.appifood_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Asegúrate de tener este import
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.appifood_movil.navigation.AppNavigation
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appifood_movil.ui.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppifoodMovilTheme {
                val searchViewModel: SearchViewModel = viewModel()

                // Se lo pasamos a la función
                AppNavigation(searchViewModel = searchViewModel)
            }
        }
    }
}
@Composable
fun RequestLocationPermission() {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // ¡Permiso concedido! Ahora puedes llamar a tu LocationManager
        } else {
            // El usuario denegó el permiso
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}