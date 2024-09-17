package me.khruslan.cryptograph.ui.coins.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import org.koin.androidx.compose.koinViewModel

internal const val COINS_ROUTE = "coins"

internal fun NavGraphBuilder.coinsScreen(
    onCoinClick: (coin: Coin) -> Unit,
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    composable(COINS_ROUTE) { navBackStackEntry ->
        val viewModel: CoinsViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        CoinsScreen(
            coinsState = viewModel.coinsState,
            onPinButtonClick = viewModel::pinCoin,
            onUnpinButtonClick = viewModel::unpinCoin,
            onRetryClick = viewModel::reloadCoins,
            onWarningShown = viewModel::warningShown,
            onCoinClick = navInterceptor(onCoinClick),
            onNotificationsActionClick = navInterceptor(onNotificationsActionClick),
            onPreferencesActionClick = navInterceptor(onPreferencesActionClick)
        )
    }
}