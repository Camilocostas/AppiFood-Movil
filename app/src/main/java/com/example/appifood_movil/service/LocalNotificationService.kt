// service/LocalNotificationService.kt
package com.example.appifood_movil.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.appifood_movil.MainActivity
import com.example.appifood_movil.R
import com.example.appifood_movil.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocalNotificationService(private val context: Context) {

    fun showOrderNotification(
        orderId: String,
        restaurantName: String,
        status: String,
        address: String
    ) {
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
            type = "order_status",
            orderId = orderId
        )
    }

    fun showPromotionNotification(title: String, body: String) {
        showNotification(
            title = title,
            body = body,
            type = "promotion"
        )
    }

    fun showRestaurantNotification(restaurantName: String, message: String) {
        showNotification(
            title = "🏪 $restaurantName",
            body = message,
            type = "restaurant_update"
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        type: String = "",
        orderId: String = ""
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("type", type)
            putExtra("orderId", orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.getChannelId())
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(context.getColor(R.color.red_primary))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // ── Método para enviar notificación con delay ──────────────────
    fun scheduleOrderNotification(
        orderId: String,
        restaurantName: String,
        status: String,
        address: String,
        delayMillis: Long = 60000 // 1 minuto por defecto
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(delayMillis)
            showOrderNotification(orderId, restaurantName, status, address)
        }
    }
}