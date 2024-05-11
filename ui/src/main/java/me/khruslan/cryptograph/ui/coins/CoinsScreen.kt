package me.khruslan.cryptograph.ui.coins

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel

internal const val COINS_ROUTE = "coins"

internal fun NavGraphBuilder.coinsScreen() {
    composable(COINS_ROUTE) {
        val viewModel: CoinsViewModel = koinViewModel()
        CoinsScreen(viewModel.coinsState)
    }
}

@Composable
internal fun CoinsScreen(coinsState: CoinsState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = coinsState.listState) {
            is CoinsListState.Loading -> Text("Loading")
            is CoinsListState.Data -> Text(state.coins.joinToString())
            is CoinsListState.Error -> Text(stringResource(state.messageRes))
        }
    }
}