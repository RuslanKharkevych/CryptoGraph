package me.khruslan.cryptograph.ui.notifications.details

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoArgs
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.COIN_ID_ARG
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.COIN_NAME_ARG
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.COIN_PRICE_ARG
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.NOTIFICATION_ID_ARG
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.NOTIFICATION_TITLE_ARG
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val NOTIFICATION_DETAILS_ROUTE = "notification-details"

@VisibleForTesting
internal object NotificationDetailsArgKeys {
    const val NOTIFICATION_ID_ARG = "notification-id"
    const val NOTIFICATION_TITLE_ARG = "notification-title"
    const val COIN_ID_ARG = "coin-id"
    const val COIN_NAME_ARG = "coin-name"
    const val COIN_PRICE_ARG = "coin-price"
}

internal data class NotificationDetailsArgs(
    val notificationId: Long,
    val notificationTitle: String?,
    override val coinId: String,
    override val coinName: String,
    override val coinPrice: String?,
) : CoinInfoArgs {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationDetailsArgs {
            return NotificationDetailsArgs(
                notificationId = checkNotNull(savedStateHandle[NOTIFICATION_ID_ARG]),
                notificationTitle = savedStateHandle[NOTIFICATION_TITLE_ARG],
                coinId = checkNotNull(savedStateHandle[COIN_ID_ARG]),
                coinName = checkNotNull(savedStateHandle[COIN_NAME_ARG]),
                coinPrice = savedStateHandle[COIN_PRICE_ARG]
            )
        }
    }
}

internal fun NavGraphBuilder.notificationDetailsScreen(
    onBackActionClick: () -> Unit,
) {
    val arguments = listOf(
        navArgument(NOTIFICATION_ID_ARG) { type = NavType.LongType; defaultValue = 0L },
        navArgument(NOTIFICATION_TITLE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ID_ARG) { type = NavType.StringType },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType },
        navArgument(COIN_PRICE_ARG) { type = NavType.StringType; nullable = true },
    )

    composable(
        route = route(NOTIFICATION_DETAILS_ROUTE, arguments),
        arguments = arguments
    ) { navBackStackEntry ->
        val viewModel: NotificationDetailsViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        NotificationDetailsScreen(
            notificationDetailsState = viewModel.notificationDetailsState,
            onRetryClick = viewModel::reloadNotification,
            onDeleteActionClick = viewModel::deleteNotification,
            onBackActionClick = navInterceptor(onBackActionClick)
        )
    }
}

internal fun NavController.navigateToNotificationDetails(
    notification: Notification?,
    coinInfo: CoinInfo,
) {
    val route = route(NOTIFICATION_DETAILS_ROUTE) {
        argument(NOTIFICATION_ID_ARG, notification?.id)
        argument(NOTIFICATION_TITLE_ARG, notification?.title)
        argument(COIN_ID_ARG, coinInfo.id)
        argument(COIN_NAME_ARG, coinInfo.name)
        argument(COIN_PRICE_ARG, coinInfo.price)
    }

    navigate(route)
}