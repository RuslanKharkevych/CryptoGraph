package me.khruslan.cryptograph.ui.coins

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.entry.entryModelOf
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.COINS
import me.khruslan.cryptograph.ui.CryptoGraphTheme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.common.CurrencyBitcoin
import me.khruslan.cryptograph.ui.common.FullScreenError
import me.khruslan.cryptograph.ui.common.FullScreenLoader
import me.khruslan.cryptograph.ui.common.StarOutline
import me.khruslan.cryptograph.ui.common.previewPlaceholder
import me.khruslan.cryptograph.ui.common.toColor

@Composable
internal fun CoinsScreen(
    coinsState: CoinsState,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
    onRetryClick: () -> Unit,
    onWarningShown: () -> Unit,
    onCoinClick: (coinId: String) -> Unit,
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    coinsState.warningMessageRes?.let { resId ->
        val snackbarMessage = stringResource(resId)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onWarningShown()
        }
    }

    Scaffold(
        topBar = {
            TopBar(onNotificationsActionClick, onPreferencesActionClick)
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
                is CoinsListState.Loading -> FullScreenLoader()

                is CoinsListState.Data -> CoinsList(
                    coins = state.coins,
                    onCoinClick = onCoinClick,
                    onPinButtonClick = onPinButtonClick,
                    onUnpinButtonClick = onUnpinButtonClick
                )

                is CoinsListState.Error -> FullScreenError(
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
    onNotificationsActionClick: () -> Unit,
    onPreferencesActionClick: () -> Unit,
) {
    TopAppBar(
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
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications_action_desc)
                )
            }
            IconButton(onClick = onPreferencesActionClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
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
    onCoinClick: (coinId: String) -> Unit,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
) {
    LazyColumn {
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
    onCoinClick: (coinId: String) -> Unit,
    onPinButtonClick: (coinId: String) -> Unit,
    onUnpinButtonClick: (coinId: String) -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = coin.colorHex.toColor().copy(alpha = 0.2f)
        ),
        onClick = { onCoinClick(coin.id) }
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
            StarIcon(
                isSelected = coin.isPinned,
                onSelected = { onPinButtonClick(coin.id) },
                onUnselected = { onUnpinButtonClick(coin.id) }
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
        Sparkline(coin.sparkline)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinTitleAndIcon(
    modifier: Modifier,
    symbol: String,
    name: String,
    iconUrl: String,
) {
    Row(modifier = modifier.basicMarquee()) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
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

@Composable
private fun StarIcon(
    isSelected: Boolean,
    onSelected: () -> Unit,
    onUnselected: () -> Unit,
) {
    if (isSelected) {
        IconButton(onClick = onUnselected) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.pin_coin_btn_desc)
            )
        }
    } else {
        IconButton(onClick = onSelected) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = stringResource(R.string.unpin_coin_btn_desc)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinPriceAndChange(
    modifier: Modifier,
    price: String,
    change: String,
    changeTrend: ChangeTrend
) {
    Row(
        modifier = modifier.basicMarquee(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = price,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
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

@Composable
private fun Sparkline(sparkline: List<Double>) {
    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        chart = lineChart(),
        model = entryModelOf(*sparkline.toTypedArray()),
        isZoomEnabled = false,
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false)
    )
}

@Composable
@PreviewLightDark
private fun CoinsScreenPreview() {
    var coins by remember { mutableStateOf(COINS) }
    val coinsState = remember {
        MutableCoinsState().apply {
            listState = CoinsListState.Data(coins)
        }
    }

    LaunchedEffect(coins) {
        coinsState.listState = CoinsListState.Data(coins)
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