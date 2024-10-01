package me.khruslan.cryptograph.ui.notifications.main

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoArgs
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoNavResultEffect
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_ICON_URL_ARG
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_ID_ARG
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_NAME_ARG
import me.khruslan.cryptograph.ui.notifications.main.NotificationsArgKeys.COIN_PRICE_ARG
import me.khruslan.cryptograph.ui.util.navigation.modal
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val NOTIFICATIONS_ROUTE = "notifications"

private object NotificationsArgKeys {
    const val COIN_ID_ARG = "coin-id"
    const val COIN_NAME_ARG = "coin-name"
    const val COIN_PRICE_ARG = "coin-price"
    const val COIN_ICON_URL_ARG = "coin-icon-url"
}

internal data class NotificationsArgs(
    val coinId: String?,
    val coinName: String?,
    val coinPrice: String?,
    val coinIconUrl: String?,
) {
    val coinSelected
        get() = coinId != null

    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationsArgs {
            return NotificationsArgs(
                coinId = savedStateHandle[COIN_ID_ARG],
                coinName = savedStateHandle[COIN_NAME_ARG],
                coinPrice = savedStateHandle[COIN_PRICE_ARG],
                coinIconUrl = savedStateHandle[COIN_ICON_URL_ARG]
            )
        }

        fun fromNavBackStackEntry(navBackStackEntry: NavBackStackEntry): NotificationsArgs {
            val bundle = checkNotNull(navBackStackEntry.arguments)
            return NotificationsArgs(
                coinId = bundle.getString(COIN_ID_ARG),
                coinName = bundle.getString(COIN_NAME_ARG),
                coinPrice = bundle.getString(COIN_PRICE_ARG),
                coinIconUrl = bundle.getString(COIN_ICON_URL_ARG)
            )
        }
    }

    fun toCoinInfoArgs(): CoinInfoArgs {
        return object : CoinInfoArgs {
            override val coinId = checkNotNull(this@NotificationsArgs.coinId)
            override val coinName = checkNotNull(this@NotificationsArgs.coinName)
            override val coinPrice = this@NotificationsArgs.coinPrice
            override val coinIconUrl = this@NotificationsArgs.coinIconUrl
        }
    }
}

private typealias NotificationDetailsCallback = (
    notification: Notification?,
    coinInfo: CoinInfo,
    coinEditable: Boolean,
) -> Unit

internal fun NavGraphBuilder.notificationsScreen(
    onNotificationDetails: NotificationDetailsCallback,
    onNotificationReport: (notification: Notification, coinInfo: CoinInfo) -> Unit,
    onCoinSelection: () -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val arguments = listOf(
        navArgument(COIN_ID_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_PRICE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ICON_URL_ARG) { type = NavType.StringType; nullable = true },
    )

    modal(
        route = route(NOTIFICATIONS_ROUTE, arguments),
        arguments = arguments,
    ) { navBackStackEntry ->
        val viewModel: NotificationsViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)
        val args = NotificationsArgs.fromNavBackStackEntry(navBackStackEntry)

        CoinInfoNavResultEffect(navBackStackEntry) { coinInfo ->
            onNotificationDetails(null, coinInfo, true)
        }

        NotificationsScreen(
            notificationsState = viewModel.notificationsState,
            onRetryClick = viewModel::reloadNotifications,
            onAddButtonClick = navInterceptor {
                if (args.coinSelected) {
                    val coinInfo = CoinInfo.fromArgs(args.toCoinInfoArgs())
                    onNotificationDetails(null, coinInfo, false)
                } else {
                    onCoinSelection()
                }
            },
            onNotificationClick = navInterceptor { (coin, notification) ->
                val coinInfo = CoinInfo.fromCoin(coin)
                if (notification.isPending) {
                    onNotificationDetails(notification, coinInfo, !args.coinSelected)
                } else {
                    onNotificationReport(notification, coinInfo)
                }
            },
            onCloseActionClick = navInterceptor(onCloseActionClick),
        )
    }
}

internal fun NavController.navigateToNotifications(coinInfo: CoinInfo? = null) {
    val route = route(NOTIFICATIONS_ROUTE) {
        argument(COIN_ID_ARG, coinInfo?.id)
        argument(COIN_NAME_ARG, coinInfo?.name)
        argument(COIN_PRICE_ARG, coinInfo?.price)
        argument(COIN_ICON_URL_ARG, coinInfo?.iconUrl)
    }

    navigate(route)
}