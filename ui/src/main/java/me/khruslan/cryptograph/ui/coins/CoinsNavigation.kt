package me.khruslan.cryptograph.ui.coins

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel

internal const val COINS_ROUTE = "coins"

internal fun NavGraphBuilder.coinsScreen(
    onCoinClick: (coinId: String) -> Unit,
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    composable(COINS_ROUTE) {
        val viewModel: CoinsViewModel = koinViewModel()
        CoinsScreen(
            coinsState = viewModel.coinsState,
            onPinButtonClick = viewModel::pinCoin,
            onUnpinButtonClick = viewModel::unpinCoin,
            onRetryClick = viewModel::reloadCoins,
            onWarningShown = viewModel::warningShown,
            onCoinClick = onCoinClick,
            onNotificationsActionClick = onNotificationsActionClick,
            onPreferencesActionClick = onPreferencesActionClick
        )
    }
}