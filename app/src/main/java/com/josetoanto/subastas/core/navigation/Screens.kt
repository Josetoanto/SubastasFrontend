package com.josetoanto.subastas.core.navigation

sealed class Screens(val route: String) {
    data object Login : Screens("login")
    data object Register : Screens("register")
    data object Home : Screens("home")
    data object CreateProduct : Screens("create_product")
    data object Profile : Screens("profile")
    data object MyAuctions : Screens("my_auctions")
    data object Favorites : Screens("favorites")
    data object Activity : Screens("activity")
    data object FlashAuctions : Screens("flash_auctions")
    data object ProductDetail : Screens("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    data object Bids : Screens("bids/{productId}") {
        fun createRoute(productId: Int) = "bids/$productId"
    }
    data object FlashBids : Screens("flash_bids/{productId}") {
        fun createRoute(productId: Int) = "flash_bids/$productId"
    }
    data object Analytics : Screens("analytics/{productId}") {
        fun createRoute(productId: Int) = "analytics/$productId"
    }
}
