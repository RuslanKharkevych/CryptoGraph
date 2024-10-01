package me.khruslan.cryptograph.ui.notifications.report

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoArgs
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.COIN_ICON_URL_ARG
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.COIN_ID_ARG
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.COIN_NAME_ARG
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.COIN_PRICE_ARG
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.NOTIFICATION_ID_ARG
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.NOTIFICATION_STATUS_ARG
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val NOTIFICATION_REPORT_ROUTE = "notification-report"

@VisibleForTesting
internal object NotificationReportArgKeys {
    const val NOTIFICATION_ID_ARG = "notification-id"
    const val NOTIFICATION_STATUS_ARG = "notification-status"
    const val COIN_ID_ARG = "coin-id"
    const val COIN_NAME_ARG = "coin-name"
    const val COIN_PRICE_ARG = "coin-price"
    const val COIN_ICON_URL_ARG = "coin-icon-url"
}

internal data class NotificationReportArgs(
    val notificationId: Long,
    val notificationStatus: NotificationStatus,
    override val coinId: String,
    override val coinName: String,
    override val coinPrice: String?,
    override val coinIconUrl: String?,
) : CoinInfoArgs {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationReportArgs {
            return NotificationReportArgs(
                notificationId = checkNotNull(savedStateHandle[NOTIFICATION_ID_ARG]),
                notificationStatus = checkNotNull(savedStateHandle[NOTIFICATION_STATUS_ARG]),
                coinId = checkNotNull(savedStateHandle[COIN_ID_ARG]),
                coinName = checkNotNull(savedStateHandle[COIN_NAME_ARG]),
                coinPrice = savedStateHandle[COIN_PRICE_ARG],
                coinIconUrl = savedStateHandle[COIN_ICON_URL_ARG]
            )
        }
    }
}

internal fun NavGraphBuilder.notificationReportDialog(
    onCloseActionClick: () -> Unit
) {
    val arguments = listOf(
        navArgument(NOTIFICATION_ID_ARG) { type = NavType.LongType },
        navArgument(NOTIFICATION_STATUS_ARG) {
            type = NavType.EnumType(NotificationStatus::class.java)
        },
        navArgument(COIN_ID_ARG) { type = NavType.StringType },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType },
        navArgument(COIN_PRICE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ICON_URL_ARG) { type = NavType.StringType; nullable = true }
    )

    dialog(
        route = route(NOTIFICATION_REPORT_ROUTE, arguments),
        arguments = arguments,
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) { navBackStackEntry ->
        val viewModel: NotificationReportViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        NotificationReportDialog(
            notificationReportState = viewModel.notificationReportState,
            onRetryClick = viewModel::reloadNotification,
            onCloseActionClick = navInterceptor(onCloseActionClick)
        )
    }
}

internal fun NavController.showNotificationReport(
    notification: Notification,
    coinInfo: CoinInfo,
) {
    val route = route(NOTIFICATION_REPORT_ROUTE) {
        argument(NOTIFICATION_ID_ARG, notification.id)
        argument(NOTIFICATION_STATUS_ARG, notification.status)
        argument(COIN_ID_ARG, coinInfo.id)
        argument(COIN_NAME_ARG, coinInfo.name)
        argument(COIN_PRICE_ARG, coinInfo.price)
        argument(COIN_ICON_URL_ARG, coinInfo.iconUrl)
    }

    navigate(route)
}