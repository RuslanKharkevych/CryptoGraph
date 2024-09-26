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
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoNavResultEffect
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.COIN_EDITABLE_ARG
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys.COIN_ICON_URL_ARG
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
    const val COIN_ICON_URL_ARG = "coin-icon-url"
    const val COIN_EDITABLE_ARG = "coin-editable"
}

internal data class NotificationDetailsArgs(
    val notificationId: Long,
    val notificationTitle: String?,
    override val coinId: String,
    override val coinName: String,
    override val coinPrice: String?,
    override val coinIconUrl: String?,
    val coinEditable: Boolean,
) : CoinInfoArgs {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationDetailsArgs {
            return NotificationDetailsArgs(
                notificationId = checkNotNull(savedStateHandle[NOTIFICATION_ID_ARG]),
                notificationTitle = savedStateHandle[NOTIFICATION_TITLE_ARG],
                coinId = checkNotNull(savedStateHandle[COIN_ID_ARG]),
                coinName = checkNotNull(savedStateHandle[COIN_NAME_ARG]),
                coinPrice = savedStateHandle[COIN_PRICE_ARG],
                coinIconUrl = savedStateHandle[COIN_ICON_URL_ARG],
                coinEditable = checkNotNull(savedStateHandle[COIN_EDITABLE_ARG])
            )
        }
    }
}

internal fun NavGraphBuilder.notificationDetailsScreen(
    onCoinFieldClick: (coinId: String) -> Unit,
    onBackActionClick: () -> Unit,
) {
    val arguments = listOf(
        navArgument(NOTIFICATION_ID_ARG) { type = NavType.LongType; defaultValue = 0L },
        navArgument(NOTIFICATION_TITLE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ID_ARG) { type = NavType.StringType },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType },
        navArgument(COIN_PRICE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ICON_URL_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_EDITABLE_ARG) { type = NavType.BoolType }
    )

    composable(
        route = route(NOTIFICATION_DETAILS_ROUTE, arguments),
        arguments = arguments
    ) { navBackStackEntry ->
        val viewModel: NotificationDetailsViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        CoinInfoNavResultEffect(navBackStackEntry) { coinInfo ->
            viewModel.updateCoinInfo(coinInfo)
        }

        NotificationDetailsScreen(
            notificationDetailsState = viewModel.notificationDetailsState,
            onRetryClick = viewModel::reloadNotification,
            onDeleteActionClick = viewModel::deleteNotification,
            onCoinFieldClick = navInterceptor(onCoinFieldClick),
            onBackActionClick = navInterceptor(onBackActionClick)
        )
    }
}

internal fun NavController.navigateToNotificationDetails(
    notification: Notification?,
    coinInfo: CoinInfo,
    coinEditable: Boolean,
) {
    val route = route(NOTIFICATION_DETAILS_ROUTE) {
        argument(NOTIFICATION_ID_ARG, notification?.id)
        argument(NOTIFICATION_TITLE_ARG, notification?.title)
        argument(COIN_ID_ARG, coinInfo.id)
        argument(COIN_NAME_ARG, coinInfo.name)
        argument(COIN_PRICE_ARG, coinInfo.price)
        argument(COIN_ICON_URL_ARG, coinInfo.iconUrl)
        argument(COIN_EDITABLE_ARG, coinEditable)
    }

    navigate(route)
}