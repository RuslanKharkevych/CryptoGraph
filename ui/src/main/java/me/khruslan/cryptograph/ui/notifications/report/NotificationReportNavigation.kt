package me.khruslan.cryptograph.ui.notifications.report

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.BundleCompat
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.coins.shared.CoinInfoArgs
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsCallback
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys.COIN_EDITABLE_ARG
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
    const val COIN_EDITABLE_ARG = "coin-editable"
}

internal data class NotificationReportArgs(
    val notificationId: Long,
    val notificationStatus: NotificationStatus,
    override val coinId: String,
    override val coinName: String,
    override val coinPrice: String?,
    override val coinIconUrl: String?,
    val coinEditable: Boolean,
) : CoinInfoArgs {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): NotificationReportArgs {
            return NotificationReportArgs(
                notificationId = checkNotNull(savedStateHandle[NOTIFICATION_ID_ARG]),
                notificationStatus = checkNotNull(savedStateHandle[NOTIFICATION_STATUS_ARG]),
                coinId = checkNotNull(savedStateHandle[COIN_ID_ARG]),
                coinName = checkNotNull(savedStateHandle[COIN_NAME_ARG]),
                coinPrice = savedStateHandle[COIN_PRICE_ARG],
                coinIconUrl = savedStateHandle[COIN_ICON_URL_ARG],
                coinEditable = checkNotNull(savedStateHandle[COIN_EDITABLE_ARG])
            )
        }

        fun fromNavBackStackEntry(navBackStackEntry: NavBackStackEntry): NotificationReportArgs {
            val bundle = checkNotNull(navBackStackEntry.arguments)
            return NotificationReportArgs(
                notificationId = checkNotNull(bundle.getLong(NOTIFICATION_ID_ARG)),
                notificationStatus = checkNotNull(
                    BundleCompat.getSerializable(
                        bundle,
                        NOTIFICATION_STATUS_ARG,
                        NotificationStatus::class.java
                    )
                ),
                coinId = checkNotNull(bundle.getString(COIN_ID_ARG)),
                coinName = checkNotNull(bundle.getString(COIN_NAME_ARG)),
                coinPrice = bundle.getString(COIN_PRICE_ARG),
                coinIconUrl = bundle.getString(COIN_ICON_URL_ARG),
                coinEditable = checkNotNull(bundle.getBoolean(COIN_EDITABLE_ARG))
            )
        }
    }
}

internal fun NavGraphBuilder.notificationReportDialog(
    onNotificationDetails: NotificationDetailsCallback,
    onDismiss: () -> Unit,
) {
    val arguments = listOf(
        navArgument(NOTIFICATION_ID_ARG) { type = NavType.LongType },
        navArgument(NOTIFICATION_STATUS_ARG) {
            type = NavType.EnumType(NotificationStatus::class.java)
        },
        navArgument(COIN_ID_ARG) { type = NavType.StringType },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType },
        navArgument(COIN_PRICE_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_ICON_URL_ARG) { type = NavType.StringType; nullable = true },
        navArgument(COIN_EDITABLE_ARG) { type = NavType.BoolType }
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
        val args = NotificationReportArgs.fromNavBackStackEntry(navBackStackEntry)

        NotificationReportDialog(
            notificationReportState = viewModel.notificationReportState,
            onRetryClick = viewModel::reloadNotification,
            onDeleteButtonClick = viewModel::deleteNotification,
            onWarningShown = viewModel::warningShown,
            onRestartButtonClick = navInterceptor { notification ->
                val coinInfo = CoinInfo.fromArgs(args)
                onNotificationDetails(notification, coinInfo, args.coinEditable)
            },
            onDismiss = navInterceptor(onDismiss)
        )
    }
}

internal typealias NotificationReportCallback = (
    notification: Notification,
    coinInfo: CoinInfo,
    coinEditable: Boolean,
) -> Unit

internal fun NavController.showNotificationReport(
    notification: Notification,
    coinInfo: CoinInfo,
    coinEditable: Boolean,
) {
    val route = route(NOTIFICATION_REPORT_ROUTE) {
        argument(NOTIFICATION_ID_ARG, notification.id)
        argument(NOTIFICATION_STATUS_ARG, notification.status)
        argument(COIN_ID_ARG, coinInfo.id)
        argument(COIN_NAME_ARG, coinInfo.name)
        argument(COIN_PRICE_ARG, coinInfo.price)
        argument(COIN_ICON_URL_ARG, coinInfo.iconUrl)
        argument(COIN_EDITABLE_ARG, coinEditable)
    }

    navigate(route)
}