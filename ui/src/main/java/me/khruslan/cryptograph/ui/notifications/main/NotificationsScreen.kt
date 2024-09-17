package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.ArrowDown
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationsScreen(
    notificationsState: NotificationsState,
    onRetryClick: () -> Unit,
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
                    // TODO: Build notifications list UI and empty state
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
private fun getTopBarTitle(coinName: String?): String {
    return if (coinName != null) {
        stringResource(R.string.coin_notifications_top_bar_title, coinName)
    } else {
        stringResource(R.string.all_notifications_top_bar_title)
    }
}

@Composable
@PreviewScreenSizesLightDark
private fun NotificationsScreenPreview() {
    val notificationsState = remember {
        val args = NotificationsArgs(
            coinId = null,
            coinName = null,
        )

        MutableNotificationsState(args).apply {
            listState = UiState.Data(PREVIEW_COIN_NOTIFICATIONS)
        }
    }

    CryptoGraphTheme {
        NotificationsScreen(
            notificationsState = notificationsState,
            onRetryClick = {},
            onCloseActionClick = {}
        )
    }
}