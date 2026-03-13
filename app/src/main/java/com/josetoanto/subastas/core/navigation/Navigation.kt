package com.josetoanto.subastas.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.josetoanto.subastas.features.auth.presentation.screens.LoginScreen
import com.josetoanto.subastas.features.auth.presentation.screens.RegisterScreen
import com.josetoanto.subastas.features.productos.presentation.screens.CreateProductoScreen
import com.josetoanto.subastas.features.productos.presentation.screens.HomeScreen
import com.josetoanto.subastas.features.productos.presentation.screens.ProductoDetailScreen
import com.josetoanto.subastas.features.profile.presentation.screens.ProfileScreen
import com.josetoanto.subastas.features.pujas.presentation.screens.PujasScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {
        composable(route = Screens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screens.Register.route)
                }
            )
        }

        composable(route = Screens.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(route = Screens.Home.route) {
            HomeScreen(
                onNavigateToDetail = { productId ->
                    navController.navigate(Screens.ProductDetail.createRoute(productId))
                },
                onNavigateToCreateProduct = {
                    navController.navigate(Screens.CreateProduct.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screens.Profile.route)
                }
            )
        }

        composable(
            route = Screens.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            ProductoDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBids = {
                    navController.navigate(Screens.Bids.createRoute(productId))
                }
            )
        }

        composable(route = Screens.CreateProduct.route) {
            CreateProductoScreen(
                onProductCreated = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screens.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.Bids.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            PujasScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
