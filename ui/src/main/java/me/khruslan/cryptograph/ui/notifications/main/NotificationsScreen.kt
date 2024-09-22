package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.managers.CoinNotification
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.ArrowDown
import me.khruslan.cryptograph.ui.util.CurrencyBitcoin
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import me.khruslan.cryptograph.ui.util.previewPlaceholder
import me.khruslan.cryptograph.ui.util.toColor
import java.time.format.DateTimeFormatter

private const val DATE_PATTERN = "dd/MM/yy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationsScreen(
    notificationsState: NotificationsState,
    onRetryClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    onNotificationClick: (notification: CoinNotification) -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                title = getTopBarTitle(notificationsState.coinName),
                onCloseActionClick = onCloseActionClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                onClick = onAddButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_notification_btn_desc)
                )
            }
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = notificationsState.listState,
            label = "NotificationsListStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> {
                    if (state.data.isEmpty()) {
                        EmptyPlaceholder(notificationsState.coinName)
                    } else {
                        NotificationsList(
                            coinNotifications = state.data,
                            onNotificationClick = onNotificationClick
                        )
                    }
                }

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
    title: String,
    onCloseActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "NotificationsTopBarShadowDpAnimation",
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
                modifier = Modifier.basicMarquee(),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseActionClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowDown,
                    contentDescription = stringResource(R.string.close_action_desc)
                )
            }
        }
    )
}

@Composable
private fun EmptyPlaceholder(coinName: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = if (coinName != null) {
                stringResource(R.string.coin_notifications_empty_placeholder, coinName)
            } else {
                stringResource(R.string.all_notifications_empty_placeholder)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun NotificationsList(
    coinNotifications: List<CoinNotification>,
    onNotificationClick: (notification: CoinNotification) -> Unit,
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(400.dp)) {
        items(
            count = coinNotifications.count(),
            key = { index -> coinNotifications[index].notification.id }
        ) { index ->
            val coinNotification = coinNotifications[index]

            NotificationCard(
                modifier = Modifier
                    .animateItem()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                coin = coinNotification.coin,
                notification = coinNotification.notification,
                onClick = { onNotificationClick(coinNotification) }
            )
        }
    }
}

@Composable
private fun NotificationCard(
    modifier: Modifier,
    coin: Coin,
    notification: Notification,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = coin.colorHex.toColor().copy(alpha = 0.2f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = notification.trigger.label,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = notification.dateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
            AsyncImage(
                modifier = Modifier.size(24.dp),
                model = coin.iconUrl,
                contentDescription = stringResource(R.string.coin_icon_desc, coin.name),
                placeholder = previewPlaceholder(Icons.Default.CurrencyBitcoin)
            )
        }
    }
}

@Composable
private fun getTopBarTitle(coinName: String?): String {
    return if (coinName != null) {
        stringResource(R.string.coin_notifications_top_bar_title, coinName)
    } else {
        stringResource(R.string.all_notifications_top_bar_title)
    }
}

private val NotificationTrigger.label: String
    @Composable
    get() {
        val resId = when (this) {
            is NotificationTrigger.PriceLessThen ->
                R.string.notification_trigger_price_less_then_desc

            is NotificationTrigger.PriceMoreThen ->
                R.string.notification_trigger_price_more_then_desc
        }
        val priceString = targetPrice.toBigDecimal().toPlainString()

        return stringResource(resId, priceString)
    }

private val Notification.dateLabel: String
    @Composable
    get() {
        val locale = getCurrentLocale()
        val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)

        val createdAtDateString = createdAt.format(dateFormatter)
        val expirationDateString = expirationDate?.format(dateFormatter)
            ?: stringResource(R.string.notification_expiration_date_not_applicable_label)

        return "$createdAtDateString - $expirationDateString"
    }

@Composable
@PreviewScreenSizesLightDark
private fun NotificationsScreenPreview() {
    val notificationsState = remember {
        val args = NotificationsArgs(
            coinId = null,
            coinName = null,
            coinPrice = null
        )

        MutableNotificationsState(args).apply {
            listState = UiState.Data(PREVIEW_COIN_NOTIFICATIONS)
        }
    }

    CryptoGraphTheme {
        NotificationsScreen(
            notificationsState = notificationsState,
            onRetryClick = {},
            onAddButtonClick = {},
            onNotificationClick = {},
            onCloseActionClick = {}
        )
    }
}