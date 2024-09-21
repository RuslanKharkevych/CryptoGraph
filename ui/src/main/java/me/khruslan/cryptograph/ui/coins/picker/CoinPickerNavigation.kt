package me.khruslan.cryptograph.ui.coins.picker

import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.ui.coins.picker.CoinPickerArgKeys.SELECTED_COIN_ID_ARG
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import me.khruslan.cryptograph.ui.util.navigation.route
import org.koin.androidx.compose.koinViewModel

private const val COIN_PICKER_ROUTE = "coin-picker"

private object CoinPickerArgKeys {
    const val SELECTED_COIN_ID_ARG = "selected-coin-id"
}

internal data class CoinPickerArgs(val selectedCoinId: String?) {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): CoinPickerArgs {
            return CoinPickerArgs(selectedCoinId = savedStateHandle[SELECTED_COIN_ID_ARG])
        }
    }
}

internal fun NavGraphBuilder.coinPickerDialog(
    onCoinSelected: (coin: Coin) -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val arguments = listOf(
        navArgument(SELECTED_COIN_ID_ARG) { type = NavType.StringType; nullable = true }
    )

    dialog(
        route = route(COIN_PICKER_ROUTE, arguments),
        arguments = arguments,
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) { navBackStackEntry ->
        val viewModel: CoinPickerViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        CoinPickerDialog(
            coinsState = viewModel.coinsState,
            onRetryClick = viewModel::reloadCoins,
            onCoinSelected = navInterceptor(onCoinSelected),
            onCloseActionClick = navInterceptor(onCloseActionClick)
        )
    }
}

internal fun NavController.showCoinPicker(selectedCoinId: String? = null) {
    val route = route(COIN_PICKER_ROUTE) {
        argument(SELECTED_COIN_ID_ARG, selectedCoinId)
    }

    navigate(route)
}