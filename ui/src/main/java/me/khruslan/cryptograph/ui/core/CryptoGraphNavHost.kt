package me.khruslan.cryptograph.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.khruslan.cryptograph.ui.coins.main.COINS_ROUTE
import me.khruslan.cryptograph.ui.coins.main.coinsScreen
import me.khruslan.cryptograph.ui.coins.history.coinHistoryScreen
import me.khruslan.cryptograph.ui.coins.history.navigateToCoinHistory

@Composable
internal fun CryptoGraphNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = COINS_ROUTE
    ) {

        coinsScreen(
            onCoinClick = navController::navigateToCoinHistory,
            onNotificationsActionClick = {
                // TODO: Navigate to the notifications screen
            },
            onPreferencesActionClick = {
                // TODO: Navigate to the preferences screen
            }
        )

        coinHistoryScreen(
            onBackActionClick = navController::popBackStack,
            onNotificationsActionClick = {
                // TODO: Navigate to the notifications screen
            }
        )
    }
}