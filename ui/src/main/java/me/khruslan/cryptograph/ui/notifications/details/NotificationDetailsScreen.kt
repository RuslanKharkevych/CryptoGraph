package me.khruslan.cryptograph.ui.notifications.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
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
import me.khruslan.cryptograph.data.fixtures.PREVIEW_NOTIFICATIONS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationDetailsScreen(
    notificationDetailsState: NotificationDetailsState,
    onRetryClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onBackActionClick: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                title = notificationDetailsState.topBarTitle,
                deleteActionVisible = notificationDetailsState.isDeletable,
                onBackActionClick = onBackActionClick,
                onDeleteActionClick = onDeleteActionClick
            )
        },
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = notificationDetailsState.notificationState,
            label = "NotificationStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> {
                    // TODO: Build notification form UI. Implement NotificationFormState class
                    //  for handling validations and other UI logic.
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
    deleteActionVisible: Boolean,
    onBackActionClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "NotificationDetailsTopBarShadowDpAnimation",
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackActionClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_action_desc)
                )
            }
        },
        actions = {
            if (deleteActionVisible) {
                IconButton(onClick = onDeleteActionClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.notifications_action_desc)
                    )
                }
            }
        }
    )
}

private val NotificationDetailsState.topBarTitle
    @Composable
    get() = notificationTitle ?: stringResource(R.string.new_notification_title, coinInfo.name)

@Composable
@PreviewScreenSizesLightDark
private fun NotificationDetailsScreenPreview() {
    val notificationState = remember {
        val args = NotificationDetailsArgs(
            notificationId = 5L,
            notificationTitle = "Solana > 200$",
            coinId = "zNZHO_Sjf",
            coinName = "Solana",
            coinPrice = "$136.43"
        )

        MutableNotificationDetailsState(args).apply {
            notificationState = UiState.Data(PREVIEW_NOTIFICATIONS[4])
        }
    }

    CryptoGraphTheme {
        NotificationDetailsScreen(
            notificationDetailsState = notificationState,
            onRetryClick = {},
            onDeleteActionClick = {},
            onBackActionClick = {}
        )
    }
}