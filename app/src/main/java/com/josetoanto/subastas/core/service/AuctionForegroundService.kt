package com.josetoanto.subastas.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.josetoanto.subastas.core.utils.parseIsoToEpochMillisOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AuctionForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "auction_live_channel"
        private const val NOTIFICATION_ID_BASE = 9000

        fun startIntent(
            context: Context,
            productId: Int,
            productName: String,
            fechaFin: String,
            esRelampago: Boolean
        ): Intent = Intent(context, AuctionForegroundService::class.java).apply {
            putExtra("product_id", productId)
            putExtra("product_name", productName)
            putExtra("fecha_fin", fechaFin)
            putExtra("es_relampago", esRelampago)
        }

        fun stopIntent(context: Context): Intent =
            Intent(context, AuctionForegroundService::class.java)
    }

    private var countdownJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val productId = intent?.getIntExtra("product_id", -1) ?: -1
        val productName = intent?.getStringExtra("product_name") ?: "Subasta"
        val fechaFin = intent?.getStringExtra("fecha_fin") ?: ""
        val esRelampago = intent?.getBooleanExtra("es_relampago", false) ?: false

        val notifId = NOTIFICATION_ID_BASE + productId
        startForeground(notifId, buildNotification(productName, "En vivo...", esRelampago))

        if (fechaFin.isNotBlank()) {
            countdownJob?.cancel()
            countdownJob = CoroutineScope(Dispatchers.IO).launch {
                val endMillis = parseIsoToEpochMillisOrNull(fechaFin)
                val nm = getSystemService(NotificationManager::class.java)
                if (endMillis == null || endMillis <= 0L) {
                    nm.notify(notifId, buildNotification(productName, "Tiempo no disponible", esRelampago))
                    return@launch
                }
                while (isActive) {
                    val remaining = (endMillis - System.currentTimeMillis()) / 1000L
                    val text = if (remaining <= 0L) {
                        "¡Subasta finalizada!"
                    } else {
                        val h = remaining / 3600
                        val m = (remaining % 3600) / 60
                        val s = remaining % 60
                        "Termina en %02d:%02d:%02d".format(h, m, s)
                    }
                    nm.notify(notifId, buildNotification(productName, text, esRelampago))
                    if (remaining <= 0L) {
                        stopSelf()
                        break
                    }
                    delay(1000L)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        countdownJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Subastas en vivo",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Notificaciones de subastas activas" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, text: String, esRelampago: Boolean): Notification {
        val label = if (esRelampago) "Subasta FLASH: $title" else "Subasta en vivo: $title"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(label)
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
