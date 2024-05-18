package me.khruslan.cryptograph.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.khruslan.cryptograph.ui.coins.COINS_ROUTE
import me.khruslan.cryptograph.ui.coins.coinsScreen

@Composable
internal fun CryptoGraphNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = COINS_ROUTE
    ) {

        coinsScreen(
            onCoinClick = {
                // TODO: Navigate to the coin screen
            },
            onNotificationsActionClick = {
                // TODO: Navigate to the notifications screen
            },
            onPreferencesActionClick = {
                // TODO: Navigate to the preferences screen
            }
        )
    }
}