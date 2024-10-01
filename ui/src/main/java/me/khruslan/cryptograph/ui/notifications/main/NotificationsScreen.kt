package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import me.khruslan.cryptograph.data.interactors.notifications.coin.CoinNotification
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.notifications.shared.NotificationPermissionState
import me.khruslan.cryptograph.ui.notifications.shared.PermissionStatus
import me.khruslan.cryptograph.ui.notifications.shared.rememberNotificationPermissionState
import me.khruslan.cryptograph.ui.notifications.shared.shouldShowRationale
import me.khruslan.cryptograph.ui.util.ArrowDown
import me.khruslan.cryptograph.ui.util.CurrencyBitcoin
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import me.khruslan.cryptograph.ui.util.previewPlaceholder
import me.khruslan.cryptograph.ui.util.state.rememberMessageState
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
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessageState = snackbarHostState.rememberMessageState()
    val permissionState = rememberNotificationPermissionState(onError = snackbarMessageState::show)
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                title = getTopBarTitle(notificationsState.coinName),
                shadowAlwaysVisible = isTopBarShadowAlwaysVisible(
                    permissionStatus = permissionState.status,
                    listState = notificationsState.listState
                ),
                onCloseActionClick = onCloseActionClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddButtonClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_notification_btn_desc)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
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
                            permissionState = permissionState,
                            coinNotifications = state.data,
                            scrollBehavior = topBarScrollBehavior,
                            onNotificationClick = onNotificationClick,
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
    shadowAlwaysVisible: Boolean,
    onCloseActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "NotificationsTopBarShadowDpAnimation",
        targetValue = if (shadowAlwaysVisible) {
            8.dp
        } else {
            8.dp * scrollBehavior.state.overlappedFraction
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsList(
    permissionState: NotificationPermissionState,
    coinNotifications: List<CoinNotification>,
    scrollBehavior: TopAppBarScrollBehavior,
    onNotificationClick: (notification: CoinNotification) -> Unit,
) {
    val permissionBannerVisible = permissionState.status is PermissionStatus.Denied

    Column {
        AnimatedVisibility(permissionBannerVisible) {
            PermissionStatusBanner(
                scrollBehavior = scrollBehavior,
                status = permissionState.status,
                onEnableButtonClick = permissionState::launchPermissionRequest,
                onSettingsButtonClick = permissionState::openNotificationSettings
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(400.dp),
            contentPadding = PaddingValues(
                top = if (permissionBannerVisible) 8.dp else 0.dp,
                bottom = 8.dp
            )
        ) {
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
}

@Composable
private fun NotificationCard(
    modifier: Modifier,
    coin: Coin,
    notification: Notification,
    onClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(
            modifier = Modifier.padding(start = 16.dp),
            visible = !notification.isPending,
            content = { Badge() }
        )
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionStatusBanner(
    scrollBehavior: TopAppBarScrollBehavior,
    status: PermissionStatus,
    onEnableButtonClick: () -> Unit,
    onSettingsButtonClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp * scrollBehavior.state.overlappedFraction
    ) {
        Crossfade(
            targetState = status,
            label = "PermissionStatusBannerCrossfade"
        ) { status ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp),
                    text = status.message,
                    style = MaterialTheme.typography.bodyLarge
                )
                TextButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp),
                    onClick = {
                        when {
                            status == PermissionStatus.Granted -> {}
                            status.shouldShowRationale -> onEnableButtonClick()
                            else -> onSettingsButtonClick()
                        }
                    }
                ) {
                    Text(
                        text = status.buttonLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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

private fun isTopBarShadowAlwaysVisible(
    permissionStatus: PermissionStatus,
    listState: UiState<List<CoinNotification>>,
): Boolean {
    if (permissionStatus is PermissionStatus.Granted) return false
    return listState is UiState.Data && listState.data.isNotEmpty()
}

private val NotificationTrigger.label: String
    @Composable
    get() {
        val resId = when (this) {
            is NotificationTrigger.PriceLessThan ->
                R.string.notification_trigger_price_less_than_desc

            is NotificationTrigger.PriceMoreThan ->
                R.string.notification_trigger_price_more_than_desc
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

private val PermissionStatus.message
    @Composable
    get() = stringResource(
        when {
            this == PermissionStatus.Granted -> R.string.notification_permission_banner_enabled_msg
            shouldShowRationale -> R.string.notification_permission_banner_rationale_msg
            else -> R.string.notification_permission_banner_disabled_msg
        }
    )

private val PermissionStatus.buttonLabel
    @Composable
    get() = when {
        this == PermissionStatus.Granted -> ""
        shouldShowRationale -> stringResource(R.string.notification_permission_banner_enable_btn)
        else -> stringResource(R.string.notification_permission_banner_settings_btn)
    }

@Composable
@PreviewScreenSizesLightDark
private fun NotificationsScreenPreview() {
    val notificationsState = remember {
        val args = NotificationsArgs(
            coinId = null,
            coinName = null,
            coinPrice = null,
            coinIconUrl = null
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