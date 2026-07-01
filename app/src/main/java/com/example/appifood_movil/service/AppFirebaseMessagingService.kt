// service/AppFirebaseMessagingService.kt
package com.example.appifood_movil.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.appifood_movil.MainActivity
import com.example.appifood_movil.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "appifood_channel"
        private const val CHANNEL_NAME = "AppiFood Notificaciones"
        private const val NOTIFICATION_ID = 1000
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Guardar token para enviar notificaciones desde el servidor
        saveTokenToPreferences(token)
        // Enviar token al servidor (si tienes backend)
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Notificación con datos personalizados
        if (message.data.isNotEmpty()) {
            handleDataMessage(message.data)
        }

        // Notificación tradicional (con título y cuerpo)
        message.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "AppiFood",
                body = notification.body ?: "Tienes una nueva notificación",
                data = message.data
            )
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return

        when (type) {
            "order_status" -> {
                val orderId = data["orderId"] ?: ""
                val restaurantName = data["restaurantName"] ?: ""
                val status = data["status"] ?: ""
                val address = data["address"] ?: ""

                val body = when (status) {
                    "dispatched" -> "El restaurante '$restaurantName' ha despachado tu pedido a la dirección: $address"
                    "preparing" -> "El restaurante '$restaurantName' está preparando tu pedido #$orderId"
                    "delivered" -> "¡Tu pedido #$orderId ha sido entregado! Disfruta tu comida 🍔"
                    "cancelled" -> "El pedido #$orderId ha sido cancelado"
                    else -> "Actualización de tu pedido #$orderId: $status"
                }

                showNotification(
                    title = "🍔 Actualización de pedido",
                    body = body,
                    data = data
                )
            }
            "promotion" -> {
                val title = data["title"] ?: "¡Nueva promoción!"
                val body = data["body"] ?: "Descubre las mejores ofertas en AppiFood"
                showNotification(title = title, body = body, data = data)
            }
            "restaurant_update" -> {
                val restaurantName = data["restaurantName"] ?: ""
                val message = data["message"] ?: ""
                showNotification(
                    title = "🏪 $restaurantName",
                    body = message,
                    data = data
                )
            }
            else -> {
                showNotification(
                    title = data["title"] ?: "AppiFood",
                    body = data["body"] ?: "Tienes una nueva notificación",
                    data = data
                )
            }
        }
    }

    private fun showNotification(title: String, body: String, data: Map<String, String> = emptyMap()) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de AppiFood"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.red_primary))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun saveTokenToPreferences(token: String) {
        val prefs = getSharedPreferences("appifood_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }

    private fun sendTokenToServer(token: String) {
        // Aquí puedes enviar el token a tu servidor para enviar notificaciones
        // Ejemplo: apiService.saveFcmToken(userId, token)
        android.util.Log.d("FCM", "Token: $token")
    }
}