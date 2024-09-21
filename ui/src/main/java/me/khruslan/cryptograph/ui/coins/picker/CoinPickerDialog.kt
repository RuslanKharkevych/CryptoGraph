package me.khruslan.cryptograph.ui.coins.picker

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COINS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinTitleAndIcon
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoinPickerDialog(
    coinsState: CoinsState,
    onRetryClick: () -> Unit,
    onCoinSelected: (coin: Coin) -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .clip(RoundedCornerShape(16.dp))
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                onCloseActionClick = onCloseActionClick
            )
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = coinsState.listState,
            label = "CoinPickerListStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> CoinsList(
                    coins = state.data,
                    selectedCoinId = coinsState.selectedCoinId,
                    onCoinClick = onCoinSelected
                )

                is UiState.Error -> FullScreenError(
                    message = stringResource(state.messageRes),
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onCloseActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "CoinPickerTopBarShadowDpAnimation",
        targetValue = 8.dp * scrollBehavior.state.overlappedFraction
    )

    TopAppBar(
        modifier = Modifier.shadow(shadow),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Text(
                text = stringResource(R.string.coin_picker_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            IconButton(onClick = onCloseActionClick) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.close_action_desc)
                )
            }
        }
    )
}

@Composable
private fun CoinsList(
    coins: List<Coin>,
    selectedCoinId: String?,
    onCoinClick: (coin: Coin) -> Unit,
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(350.dp)) {
        items(
            count = coins.count(),
            key = { index -> coins[index].id }
        ) { index ->
            val coin = coins[index]

            CoinItem(
                coin = coin,
                isSelected = coin.id == selectedCoinId,
                onClick = { onCoinClick(coin) }
            )
        }
    }
}

@Composable
private fun CoinItem(
    coin: Coin,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                coin.colorHex.toColor().copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        headlineContent = {
            CoinTitleAndIcon(
                symbol = coin.symbol,
                name = coin.name,
                iconUrl = coin.iconUrl
            )
        }
    )
}

@Composable
@PreviewScreenSizesLightDark
private fun CoinPickerDialogPreview() {
    val coinsState = remember {
        val args = CoinPickerArgs(selectedCoinId = PREVIEW_COINS[3].id)

        MutableCoinsState(args).apply {
            listState = UiState.Data(PREVIEW_COINS)
        }
    }

    @Composable
    fun DimOverlay(content: @Composable BoxScope.() -> Unit) {
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f)),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }

    CryptoGraphTheme {
        DimOverlay {
            CoinPickerDialog(
                coinsState = coinsState,
                onRetryClick = {},
                onCoinSelected = {},
                onCloseActionClick = {}
            )
        }
    }
}