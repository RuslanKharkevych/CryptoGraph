package me.khruslan.cryptograph.ui.notifications.report

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationReportDialog(
    notificationReportState: NotificationReportState,
    onRetryClick: () -> Unit,
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

                is UiState.Data -> {
                    // TODO: Build notification report UI
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
private fun getTopBarTitle(notificationStatus: NotificationStatus): String {
    return when (notificationStatus) {
        NotificationStatus.Completed ->
            stringResource(R.string.notification_report_completed_top_bar_title)

        NotificationStatus.Expired ->
            stringResource(R.string.notification_report_expired_top_bar_title)

        NotificationStatus.Pending ->
            throw IllegalArgumentException("Notification must not be pending")
    }
}