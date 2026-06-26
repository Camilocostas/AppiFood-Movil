// utils/NotificationHelper.kt
package com.example.appifood_movil.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.appifood_movil.R

object NotificationHelper {
    private const val CHANNEL_ID = "appifood_channel"
    private const val CHANNEL_NAME = "AppiFood Notificaciones"
    private const val CHANNEL_DESCRIPTION = "Notificaciones de pedidos y promociones"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getChannelId(): String = CHANNEL_ID
}