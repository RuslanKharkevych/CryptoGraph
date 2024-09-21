package me.khruslan.cryptograph.ui.notifications.main

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_ID_ARG
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_NAME_ARG
import me.khruslan.cryptograph.ui.util.navigation.modal
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val NOTIFICATIONS_ROUTE = "notifications"

private object NotificationsArgKeys {
    const val COIN_ID_ARG = "coin-id"
    const val COIN_NAME_ARG = "coin-name"
}

internal data class NotificationsArgs(
    val coinId: String?,
    val coinName: String?,
) {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationsArgs {
            return NotificationsArgs(
                coinId = savedStateHandle[COIN_ID_ARG],
                coinName = savedStateHandle[COIN_NAME_ARG]
            )
        }

        fun fromNavBackStackEntry(navBackStackEntry: NavBackStackEntry): NotificationsArgs {
            val bundle = checkNotNull(navBackStackEntry.arguments)
            return NotificationsArgs(
                coinId = bundle.getString(COIN_ID_ARG),
                coinName = bundle.getString(COIN_NAME_ARG)
            )
        }
    }
}

internal fun NavGraphBuilder.notificationsScreen(
    onCoinSelection: () -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val arguments = listOf(
        navArgument(COIN_ID_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType; nullable = true }
    )

    modal(
        route = route(NOTIFICATIONS_ROUTE, arguments),
        arguments = arguments,
    ) { navBackStackEntry ->
        val viewModel: NotificationsViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)
        val args = NotificationsArgs.fromNavBackStackEntry(navBackStackEntry)

        NotificationsScreen(
            notificationsState = viewModel.notificationsState,
            onRetryClick = viewModel::reloadNotifications,
            onCloseActionClick = navInterceptor(onCloseActionClick),
            onAddButtonClick = {
                if (args.coinId != null) {
                    // TODO: Navigate to notification details screen
                } else {
                    onCoinSelection()
                }
            }
        )
    }
}

internal fun NavController.navigateToNotifications(
    coinId: String? = null,
    coinName: String? = null,
) {
    val route = route(NOTIFICATIONS_ROUTE) {
        argument(COIN_ID_ARG, coinId)
        argument(COIN_NAME_ARG, coinName)
    }

    navigate(route)
}