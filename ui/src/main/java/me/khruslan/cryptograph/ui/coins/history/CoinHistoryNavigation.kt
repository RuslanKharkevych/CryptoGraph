package me.khruslan.cryptograph.ui.coins.history

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryArgKeys.COIN_ID_ARG
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryArgKeys.COIN_NAME_ARG
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryArgKeys.COLOR_HEX_ARG
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryArgKeys.IS_PINNED_ARG
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val COIN_HISTORY_ROUTE = "coin-history"

@VisibleForTesting
internal object CoinHistoryArgKeys {
    const val COIN_ID_ARG = "coin-id"
    const val COIN_NAME_ARG = "coin-name"
    const val COLOR_HEX_ARG = "color-hex"
    const val IS_PINNED_ARG = "is-pinned"
}

internal data class CoinHistoryArgs(
    val coinId: String,
    val coinName: String,
    val colorHex: String?,
    val isPinned: Boolean,
) {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): CoinHistoryArgs {
            return CoinHistoryArgs(
                coinId = checkNotNull(savedStateHandle[COIN_ID_ARG]),
                coinName = checkNotNull(savedStateHandle[COIN_NAME_ARG]),
                colorHex = savedStateHandle[COLOR_HEX_ARG],
                isPinned = checkNotNull(savedStateHandle[IS_PINNED_ARG])
            )
        }

        fun fromNavBackStackEntry(navBackStackEntry: NavBackStackEntry): CoinHistoryArgs {
            val bundle = checkNotNull(navBackStackEntry.arguments)
            return CoinHistoryArgs(
                coinId = checkNotNull(bundle.getString(COIN_ID_ARG)),
                coinName = checkNotNull(bundle.getString(COIN_NAME_ARG)),
                colorHex = bundle.getString(COLOR_HEX_ARG),
                isPinned = checkNotNull(bundle.getBoolean(IS_PINNED_ARG))
            )
        }
    }
}

internal fun NavGraphBuilder.coinHistoryScreen(
    onBackActionClick: () -> Unit,
    onNotificationsActionClick: (coinId: String, coinName: String) -> Unit,
) {
    val arguments = listOf(
        navArgument(COIN_ID_ARG) { type = NavType.StringType },
        navArgument(COIN_NAME_ARG) { type = NavType.StringType },
        navArgument(COLOR_HEX_ARG) { type = NavType.StringType; nullable = true },
        navArgument(IS_PINNED_ARG) { type = NavType.BoolType }
    )

    composable(
        route = route(COIN_HISTORY_ROUTE, arguments),
        arguments = arguments
    ) { navBackStackEntry ->
        val viewModel: CoinHistoryViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)
        val args = CoinHistoryArgs.fromNavBackStackEntry(navBackStackEntry)

        CoinHistoryScreen(
            coinHistoryState = viewModel.coinHistoryState,
            onPinActionClick = viewModel::pinCoin,
            onUnpinActionClick = viewModel::unpinCoin,
            onRetryClick = viewModel::reloadCoinHistory,
            onWarningShown = viewModel::warningShown,
            onBackActionClick = navInterceptor(onBackActionClick),
            onNotificationsActionClick = navInterceptor {
                onNotificationsActionClick(args.coinId, args.coinName)
            }
        )
    }
}

internal fun NavController.navigateToCoinHistory(coin: Coin) {
    val route = route(COIN_HISTORY_ROUTE) {
        argument(COIN_ID_ARG, coin.id)
        argument(COIN_NAME_ARG, coin.name)
        argument(COLOR_HEX_ARG, coin.colorHex)
        argument(IS_PINNED_ARG, coin.isPinned)
    }

    navigate(route)
}