package com.josetoanto.subastas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.josetoanto.subastas.core.navigation.Navigation
import com.josetoanto.subastas.core.navigation.Screens
import com.josetoanto.subastas.core.ui.theme.SubastasTheme
import com.josetoanto.subastas.core.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null
    private val pendingDeepLink = mutableStateOf<Pair<Int, String?>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupBackgroundSync()

        val initialDeepLink = extractDeepLink(intent)

        setContent {
            SubastasTheme {
                val navController = rememberNavController().also {
                    this.navController = it
                }
                Navigation(navController = navController)

                LaunchedEffect(Unit) {
                    initialDeepLink?.let { (productId, type) ->
                        navigateFromFcm(navController, productId, type)
                    }
                }
                LaunchedEffect(pendingDeepLink.value) {
                    pendingDeepLink.value?.let { (productId, type) ->
                        navigateFromFcm(navController, productId, type)
                        pendingDeepLink.value = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractDeepLink(intent)?.let {
            pendingDeepLink.value = it
        }
    }

    private fun extractDeepLink(intent: Intent?): Pair<Int, String?>? {
        val productId = intent?.getIntExtra("fcm_product_id", -1)?.takeIf { it != -1 }
        val type = intent?.getStringExtra("fcm_type")
        return if (productId != null) Pair(productId, type) else null
    }

    private fun navigateFromFcm(
        navController: NavHostController,
        productId: Int,
        type: String?
    ) {
        when (type) {
            "ganador", "superado", "cierre_proximo" ->
                navController.navigate(Screens.Bids.createRoute(productId)) {
                    launchSingleTop = true
                }
            else ->
                navController.navigate(Screens.ProductDetail.createRoute(productId)) {
                    launchSingleTop = true
                }
        }
    }

    private fun setupBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncSubastasWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}