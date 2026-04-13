package com.josetoanto.subastas.core.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.josetoanto.subastas.MainActivity

class SubastasFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Enviar token al backend cuando el endpoint esté disponible
        // POST /api/v1/usuarios/fcm-token  { token: token }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannels()

        val type = message.data["tipo"] ?: "general"
        val title = message.notification?.title ?: message.data["title"] ?: "Subastas"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val productId = message.data["producto_id"]?.toIntOrNull()

        val channelId = when (type) {
            "ganador" -> "won_channel"
            "superado" -> "outbid_channel"
            "cierre_proximo" -> "expiring_channel"
            "geo" -> "near_channel"
            else -> "general_channel"
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            productId?.let { putExtra("fcm_product_id", it) }
            putExtra("fcm_type", type)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            productId ?: System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(
                when (type) {
                    "won", "outbid" -> NotificationCompat.PRIORITY_HIGH
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            listOf(
                Triple("won_channel", "Subasta ganada", NotificationManager.IMPORTANCE_HIGH),
                Triple("outbid_channel", "Superado en puja", NotificationManager.IMPORTANCE_HIGH),
                Triple("expiring_channel", "Subasta por terminar", NotificationManager.IMPORTANCE_DEFAULT),
                Triple("near_channel", "Subasta cercana", NotificationManager.IMPORTANCE_DEFAULT),
                Triple("general_channel", "General", NotificationManager.IMPORTANCE_LOW)
            ).forEach { (id, name, importance) ->
                if (manager.getNotificationChannel(id) == null) {
                    manager.createNotificationChannel(NotificationChannel(id, name, importance))
                }
            }
        }
    }
}
