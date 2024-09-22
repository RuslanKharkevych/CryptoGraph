package me.khruslan.cryptograph.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.khruslan.cryptograph.ui.coins.history.coinHistoryScreen
import me.khruslan.cryptograph.ui.coins.history.navigateToCoinHistory
import me.khruslan.cryptograph.ui.coins.main.COINS_ROUTE
import me.khruslan.cryptograph.ui.coins.main.coinsScreen
import me.khruslan.cryptograph.ui.coins.picker.coinPickerDialog
import me.khruslan.cryptograph.ui.coins.picker.showCoinPicker
import me.khruslan.cryptograph.ui.coins.shared.dismissWithSelectedCoin
import me.khruslan.cryptograph.ui.notifications.details.navigateToNotificationDetails
import me.khruslan.cryptograph.ui.notifications.details.notificationDetailsScreen
import me.khruslan.cryptograph.ui.notifications.main.navigateToNotifications
import me.khruslan.cryptograph.ui.notifications.main.notificationsScreen
import me.khruslan.cryptograph.ui.util.navigation.Transitions

@Composable
internal fun CryptoGraphNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = COINS_ROUTE,
        enterTransition = Transitions.enter,
        exitTransition = Transitions.exit,
        popEnterTransition = Transitions.popEnter,
        popExitTransition = Transitions.popExit
    ) {

        coinsScreen(
            onCoinClick = navController::navigateToCoinHistory,
            onNotificationsActionClick = navController::navigateToNotifications,
            onPreferencesActionClick = {
                // TODO: Navigate to the preferences screen
            }
        )

        coinHistoryScreen(
            onBackActionClick = navController::popBackStack,
            onNotificationsActionClick = navController::navigateToNotifications
        )

        coinPickerDialog(
            onCoinSelected = navController::dismissWithSelectedCoin,
            onCloseActionClick = navController::popBackStack
        )

        notificationsScreen(
            onNotificationDetails = navController::navigateToNotificationDetails,
            onCoinSelection = navController::showCoinPicker,
            onCloseActionClick = navController::popBackStack
        )

        notificationDetailsScreen(
            onBackActionClick = navController::popBackStack
        )
    }
}