package com.josetoanto.subastas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.josetoanto.subastas.core.navigation.Navigation
import com.josetoanto.subastas.core.navigation.Screens
import com.josetoanto.subastas.core.ui.theme.SubastasTheme
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.josetoanto.subastas.core.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint

//Antonio
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fcmProductId = intent?.getIntExtra("fcm_product_id", -1)?.takeIf { it != -1 }
        val fcmType = intent?.getStringExtra("fcm_type")

        // Setup background synchronization
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(syncConstraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncSubastasWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        setContent {
            SubastasTheme {
                val navController = rememberNavController()
                Navigation(navController = navController)

                // Deep-link desde notificación FCM
                LaunchedEffect(Unit) {
                    if (fcmProductId != null) {
                        when (fcmType) {
                            "ganador", "superado", "cierre_proximo" ->
                                navController.navigate(Screens.Bids.createRoute(fcmProductId))
                            "geo" ->
                                navController.navigate(Screens.ProductDetail.createRoute(fcmProductId))
                            else ->
                                navController.navigate(Screens.ProductDetail.createRoute(fcmProductId))
                        }
                    }
                }
            }
        }
    }
}
