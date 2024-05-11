package me.khruslan.cryptograph.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.khruslan.cryptograph.ui.coins.COINS_ROUTE
import me.khruslan.cryptograph.ui.coins.coinsScreen

class CryptoGraphActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CryptoGraphNavigation()
        }
    }
}

@Composable
private fun CryptoGraphNavigation() {
    val navController = rememberNavController()

    NavHost(navController, COINS_ROUTE) {
        coinsScreen()
    }
}