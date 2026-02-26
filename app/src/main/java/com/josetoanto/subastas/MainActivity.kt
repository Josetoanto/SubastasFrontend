package com.josetoanto.subastas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.josetoanto.subastas.core.navigation.Navigation
import com.josetoanto.subastas.core.ui.theme.SubastasTheme
import dagger.hilt.android.AndroidEntryPoint

//Antonio
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubastasTheme {
                val navController = rememberNavController()
                Navigation(navController = navController)
            }
        }
    }
}
