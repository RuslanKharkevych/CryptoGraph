package me.khruslan.cryptograph.ui.core

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.khruslan.cryptograph.base.Logger
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
import me.khruslan.cryptograph.ui.notifications.report.notificationReportDialog
import me.khruslan.cryptograph.ui.notifications.report.showNotificationReport
import me.khruslan.cryptograph.ui.preferences.main.navigateToPreferences
import me.khruslan.cryptograph.ui.preferences.main.preferencesScreen
import me.khruslan.cryptograph.ui.util.navigation.Transitions

private const val LOG_TAG = "CryptoGraphNavHost"

@Composable
internal fun CryptoGraphNavHost() {
    val navController = rememberNavController()

    DisposableEffect(navController) {
        val listener = DestinationListener()
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

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
            onPreferencesActionClick = navController::navigateToPreferences
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
            onNotificationReport = navController::showNotificationReport,
            onCoinSelection = navController::showCoinPicker,
            onCloseActionClick = navController::popBackStack
        )

        notificationDetailsScreen(
            onCoinFieldClick = navController::showCoinPicker,
            onCloseScreen = navController::popBackStack
        )

        notificationReportDialog(
            onNotificationDetails = navController::navigateToNotificationDetails,
            onDismiss = navController::popBackStack
        )

        preferencesScreen(
            onBackActionClick = navController::popBackStack
        )
    }
}

private class DestinationListener : NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        val route = destination.route?.substringBefore('?')
        Logger.info(LOG_TAG, "Destination changed: $route")
    }
}