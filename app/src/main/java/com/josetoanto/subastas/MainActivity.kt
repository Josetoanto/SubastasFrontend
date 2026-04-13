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
import dagger.hilt.android.AndroidEntryPoint

//Antonio
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fcmProductId = intent?.getIntExtra("fcm_product_id", -1)?.takeIf { it != -1 }
        val fcmType = intent?.getStringExtra("fcm_type")

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
