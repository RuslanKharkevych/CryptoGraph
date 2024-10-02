package me.khruslan.cryptograph.ui.notifications.report

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COINS
import me.khruslan.cryptograph.data.fixtures.PREVIEW_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.notifications.shared.description
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import me.khruslan.cryptograph.ui.util.preview.DimOverlay
import me.khruslan.cryptograph.ui.util.preview.PreviewScreenSizesLightDark
import java.time.format.DateTimeFormatter

private const val DATE_PATTERN = "MMMM dd, yyyy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationReportDialog(
    notificationReportState: NotificationReportState,
    onRetryClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onRestartButtonClick: () -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .clip(MaterialTheme.shapes.large)
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                title = getTopBarTitle(notificationReportState.notificationStatus),
                onCloseActionClick = onCloseActionClick
            )
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = notificationReportState.notificationState,
            label = "NotificationStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> NotificationReport(
                    notification = state.data,
                    coinInfo = notificationReportState.coinInfo,
                    onDeleteButtonClick = onDeleteButtonClick,
                    onRestartButtonClick = onRestartButtonClick
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
    title: String,
    onCloseActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "NotificationReportTopBarShadowDpAnimation",
        targetValue = 8.dp * scrollBehavior.state.overlappedFraction
    )

    TopAppBar(
        modifier = Modifier.shadow(shadow),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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
private fun NotificationReport(
    notification: Notification,
    coinInfo: CoinInfo,
    onDeleteButtonClick: () -> Unit,
    onRestartButtonClick: () -> Unit,
) {
    val reportItems = rememberNotificationReportItems(notification, coinInfo)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columnsCount = if (maxWidth < 700.dp) 1 else 2

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                reportItems.chunked(columnsCount).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { item ->
                            NotificationReportItem(
                                modifier = Modifier.weight(1f),
                                item = item
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            OvertopBlurOverlay(
                height = 32.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                NotificationReportButtons(
                    onDeleteButtonClick = onDeleteButtonClick,
                    onRestartButtonClick = onRestartButtonClick
                )
            }
        }
    }
}

@Composable
private fun NotificationReportItem(
    modifier: Modifier,
    item: NotificationReportItem,
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        overlineContent = {
            Text(item.label)
        },
        headlineContent = {
            Text(item.value)
        }
    )
}

@Composable
private fun NotificationReportButtons(
    onDeleteButtonClick: () -> Unit,
    onRestartButtonClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        val buttonModifier = Modifier
            .sizeIn(minHeight = 45.dp)
            .weight(1f)

        OutlinedButton(
            modifier = buttonModifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            onClick = onDeleteButtonClick,
            content = {
                Text(text = stringResource(R.string.delete_btn))
            }
        )

        OutlinedButton(
            modifier = buttonModifier,
            onClick = onRestartButtonClick,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            content = {
                Text(text = stringResource(R.string.restart_btn))
            }
        )
    }
}

@Composable
private fun OvertopBlurOverlay(
    height: Dp,
    color: Color,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val heightPx = height.toPx()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, color),
                        startY = -heightPx,
                        endY = 0f,
                    ),
                    topLeft = Offset(x = 0f, y = -heightPx),
                    size = size.copy(height = heightPx)
                )
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun rememberNotificationReportItems(
    notification: Notification,
    coinInfo: CoinInfo,
): List<NotificationReportItem> {
    val locale = getCurrentLocale()
    val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)

    return listOf(
        NotificationReportItem(
            label = stringResource(R.string.notification_title_label),
            value = notification.title
        ),
        NotificationReportItem(
            label = stringResource(R.string.coin_name_label),
            value = coinInfo.name
        ),
        NotificationReportItem(
            label = stringResource(R.string.notification_trigger_label),
            value = notification.trigger.description
        ),
        NotificationReportItem(
            label = stringResource(R.string.notification_status_label),
            value = notification.status.title
        ),
        NotificationReportItem(
            label = stringResource(R.string.created_at_label),
            value = notification.createdAt.format(dateFormatter)
        ),
        NotificationReportItem(
            label = getEndDateLabel(notification.status),
            value = notification.endDate.format(dateFormatter)
        )
    )
}

private data class NotificationReportItem(
    val label: String,
    val value: String,
)

private class UnexpectedNotificationStatusException :
    IllegalArgumentException("Notification must not be pending")

@Composable
private fun getTopBarTitle(notificationStatus: NotificationStatus): String {
    return stringResource(
        when (notificationStatus) {
            NotificationStatus.Completed -> R.string.notification_report_completed_top_bar_title
            NotificationStatus.Expired -> R.string.notification_report_expired_top_bar_title
            NotificationStatus.Pending -> throw UnexpectedNotificationStatusException()
        }
    )
}

@Composable
private fun getEndDateLabel(notificationStatus: NotificationStatus): String {
    return stringResource(
        when (notificationStatus) {
            NotificationStatus.Completed -> R.string.completed_at_label
            NotificationStatus.Expired -> R.string.expired_at_label
            NotificationStatus.Pending -> throw UnexpectedNotificationStatusException()
        }
    )
}

private val NotificationStatus.title
    @Composable
    get() = stringResource(
        when (this) {
            NotificationStatus.Completed -> R.string.notification_status_completed
            NotificationStatus.Expired -> R.string.notification_status_expired
            NotificationStatus.Pending -> throw UnexpectedNotificationStatusException()
        }
    )

private val Notification.endDate
    get() = when (status) {
        NotificationStatus.Completed -> checkNotNull(completedAt)
        NotificationStatus.Expired -> checkNotNull(expirationDate)
        NotificationStatus.Pending -> throw UnexpectedNotificationStatusException()
    }

@Composable
@PreviewScreenSizesLightDark
private fun NotificationReportDialogPreview() {
    val notificationReportState = remember {
        val notification = PREVIEW_NOTIFICATIONS[0]
        val coin = PREVIEW_COINS[0]

        val args = NotificationReportArgs(
            notificationId = notification.id,
            notificationStatus = notification.status,
            coinId = coin.id,
            coinName = coin.name,
            coinPrice = coin.price,
            coinIconUrl = coin.iconUrl
        )

        MutableNotificationReportState(args).apply {
            notificationState = UiState.Data(notification)
        }
    }

    CryptoGraphTheme {
        DimOverlay {
            NotificationReportDialog(
                notificationReportState = notificationReportState,
                onRetryClick = {},
                onDeleteButtonClick = {},
                onRestartButtonClick = {},
                onCloseActionClick = {}
            )
        }
    }
}