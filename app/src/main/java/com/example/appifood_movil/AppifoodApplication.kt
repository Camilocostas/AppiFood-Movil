package com.example.appifood_movil

import android.app.Application
import com.example.appifood_movil.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppifoodApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)
    }
}