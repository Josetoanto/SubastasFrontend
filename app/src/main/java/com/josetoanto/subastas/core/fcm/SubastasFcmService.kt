package com.josetoanto.subastas.core.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.josetoanto.subastas.MainActivity

class SubastasFcmService : FirebaseMessagingService() {

    companion object {
        const val TYPE_GANADOR  = "ganador"
        const val TYPE_SUPERADO = "superado"
        const val TYPE_CIERRE   = "cierre_proximo"
        const val TYPE_GEO      = "geo"
        const val TYPE_GENERAL  = "general"

        const val CHANNEL_WON      = "won_channel"
        const val CHANNEL_OUTBID   = "outbid_channel"
        const val CHANNEL_EXPIRING = "expiring_channel"
        const val CHANNEL_NEAR     = "near_channel"
        const val CHANNEL_GENERAL  = "general_channel"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannels()

        val type      = message.data["tipo"] ?: TYPE_GENERAL
        val title     = message.notification?.title ?: message.data["title"] ?: defaultTitleForType(type)
        val body      = message.notification?.body  ?: message.data["body"]  ?: ""
        val productId = message.data["producto_id"]?.toIntOrNull()
        val channelId = channelForType(type)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            productId?.let { putExtra("fcm_product_id", it) }
            putExtra("fcm_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (productId ?: 0) + type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (type) {
            TYPE_GANADOR, TYPE_SUPERADO -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(priority)
            .setDefaults(
                if (type == TYPE_GANADOR || type == TYPE_SUPERADO)
                    NotificationCompat.DEFAULT_ALL
                else
                    NotificationCompat.DEFAULT_LIGHTS
            )
            .build()

        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!hasPermission) return

        try {
            NotificationManagerCompat.from(this)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun channelForType(type: String): String = when (type) {
        TYPE_GANADOR  -> CHANNEL_WON
        TYPE_SUPERADO -> CHANNEL_OUTBID
        TYPE_CIERRE   -> CHANNEL_EXPIRING
        TYPE_GEO      -> CHANNEL_NEAR
        else          -> CHANNEL_GENERAL
    }

    private fun defaultTitleForType(type: String): String = when (type) {
        TYPE_GANADOR  -> "¡Felicidades, ganaste!"
        TYPE_SUPERADO -> "Te superaron en la puja"
        TYPE_CIERRE   -> "Subasta por terminar"
        TYPE_GEO      -> "Subasta cercana disponible"
        else          -> "Subastas"
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            listOf(
                Triple(CHANNEL_WON,      "Subasta ganada",       NotificationManager.IMPORTANCE_HIGH),
                Triple(CHANNEL_OUTBID,   "Superado en puja",     NotificationManager.IMPORTANCE_HIGH),
                Triple(CHANNEL_EXPIRING, "Subasta por terminar", NotificationManager.IMPORTANCE_DEFAULT),
                Triple(CHANNEL_NEAR,     "Subasta cercana",      NotificationManager.IMPORTANCE_DEFAULT),
                Triple(CHANNEL_GENERAL,  "General",              NotificationManager.IMPORTANCE_LOW)
            ).forEach { (id, name, importance) ->
                if (manager.getNotificationChannel(id) == null) {
                    manager.createNotificationChannel(NotificationChannel(id, name, importance))
                }
            }
        }
    }
}