package com.example.appifood_movil

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import timber.log.Timber
import com.google.firebase.BuildConfig

@HiltAndroidApp
class AppifoodApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar MapLibre (sin API Key, usa OpenStreetMap vía estilo MapLibre)
        MapLibre.getInstance(this, null, WellKnownTileServer.MapLibre)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Timber solo en debug
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}