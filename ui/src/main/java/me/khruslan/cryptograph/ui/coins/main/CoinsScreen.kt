package me.khruslan.cryptograph.ui.coins.main

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.entryModelOf
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COINS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.PinCoinButton
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.core.DarkGreen
import me.khruslan.cryptograph.ui.core.DarkRed
import me.khruslan.cryptograph.ui.core.DarkYellow
import me.khruslan.cryptograph.ui.util.CurrencyBitcoin
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.TrendingDown
import me.khruslan.cryptograph.ui.util.TrendingFlat
import me.khruslan.cryptograph.ui.util.TrendingUp
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.previewPlaceholder
import me.khruslan.cryptograph.ui.util.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoinsScreen(
    coinsState: CoinsState,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
    onRetryClick: () -> Unit,
    onWarningShown: () -> Unit,
    onCoinClick: (coin: Coin) -> Unit,
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    coinsState.warningMessageRes?.let { resId ->
        val snackbarMessage = stringResource(resId)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onWarningShown()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                onNotificationsActionClick = onNotificationsActionClick,
                onPreferencesActionClick = onPreferencesActionClick
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = coinsState.listState,
            label = "CoinsListStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> CoinsList(
                    coins = state.data,
                    onCoinClick = onCoinClick,
                    onPinButtonClick = onPinButtonClick,
                    onUnpinButtonClick = onUnpinButtonClick
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
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "TopBarShadowDpAnimation",
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
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            IconButton(onClick = onNotificationsActionClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications_action_desc)
                )
            }
            IconButton(onClick = onPreferencesActionClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.preferences_action_desc)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinsList(
    coins: List<Coin>,
    onCoinClick: (coin: Coin) -> Unit,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(400.dp)) {
        items(
            count = coins.count(),
            key = { index -> coins[index].id }
        ) { index ->
            CoinCard(
                modifier = Modifier
                    .animateItemPlacement()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                coin = coins[index],
                onCoinClick = onCoinClick,
                onPinButtonClick = onPinButtonClick,
                onUnpinButtonClick = onUnpinButtonClick
            )
        }
    }
}

@Composable
private fun CoinCard(
    modifier: Modifier,
    coin: Coin,
    onCoinClick: (coin: Coin) -> Unit,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = coin.colorHex.toColor().copy(alpha = 0.2f)
        ),
        onClick = { onCoinClick(coin) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoinTitleAndIcon(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                symbol = coin.symbol,
                name = coin.name,
                iconUrl = coin.iconUrl
            )
            PinCoinButton(
                isPinned = coin.isPinned,
                onPin = { onPinButtonClick(coin.id) },
                onUnpin = { onUnpinButtonClick(coin.id) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            CoinPriceAndChange(
                modifier = Modifier.weight(1f),
                price = coin.price,
                change = coin.change,
                changeTrend = coin.changeTrend
            )
        }
        if (coin.sparkline.isNotEmpty()) {
            Sparkline(coin.sparkline)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinTitleAndIcon(
    modifier: Modifier,
    symbol: String?,
    name: String,
    iconUrl: String?,
) {
    Row(modifier = modifier.basicMarquee()) {
        if (symbol != null) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
        if (iconUrl != null) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                model = iconUrl,
                contentDescription = stringResource(R.string.coin_icon_desc, name),
                placeholder = previewPlaceholder(Icons.Default.CurrencyBitcoin)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinPriceAndChange(
    modifier: Modifier,
    price: String?,
    change: String?,
    changeTrend: ChangeTrend
) {
    Row(
        modifier = modifier.basicMarquee(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (price != null) {
            Text(
                text = price,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (change != null) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = changeTrend.icon,
                contentDescription = stringResource(changeTrend.contendDescRes),
                tint = changeTrend.color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = change,
                style = MaterialTheme.typography.labelMedium,
                color = changeTrend.color,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun Sparkline(sparkline: List<Double>) {
    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        chart = lineChart(),
        model = entryModelOf(*sparkline.toTypedArray()),
        isZoomEnabled = false,
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
        horizontalLayout = HorizontalLayout.FullWidth()
    )
}

private val ChangeTrend.icon
    get() = when (this) {
        ChangeTrend.UP -> Icons.AutoMirrored.Default.TrendingUp
        ChangeTrend.DOWN -> Icons.AutoMirrored.Default.TrendingDown
        ChangeTrend.STEADY_OR_UNKNOWN -> Icons.AutoMirrored.Default.TrendingFlat
    }

@get:StringRes
private val ChangeTrend.contendDescRes
    get() = when (this) {
        ChangeTrend.UP -> R.string.trending_up_icon_desc
        ChangeTrend.DOWN -> R.string.trending_down_icon_desc
        ChangeTrend.STEADY_OR_UNKNOWN -> R.string.trending_flat_icon_desc
    }

private val ChangeTrend.color
    get() = when (this) {
        ChangeTrend.UP -> DarkGreen
        ChangeTrend.DOWN -> DarkRed
        ChangeTrend.STEADY_OR_UNKNOWN -> DarkYellow
    }

@Composable
@PreviewScreenSizesLightDark
private fun CoinsScreenPreview() {
    var coins by remember { mutableStateOf(PREVIEW_COINS) }
    val coinsState = remember {
        MutableCoinsState().apply {
            listState = UiState.Data(coins)
        }
    }

    LaunchedEffect(coins) {
        coinsState.listState = UiState.Data(coins)
    }

    fun updateCoin(id: String, isPinned: Boolean) {
        coins = coins.map { coin ->
            if (id == coin.id) coin.copy(isPinned = isPinned) else coin
        }.sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
    }

    CryptoGraphTheme {
        CoinsScreen(
            coinsState = coinsState,
            onPinButtonClick = { updateCoin(it, true) },
            onUnpinButtonClick = { updateCoin(it, false) },
            onRetryClick = {},
            onWarningShown = {},
            onCoinClick = {},
            onNotificationsActionClick = {},
            onPreferencesActionClick = {}
        )
    }
}