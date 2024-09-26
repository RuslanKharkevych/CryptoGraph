package me.khruslan.cryptograph.ui.notifications.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.fixtures.PREVIEW_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.notifications.details.date.ExpirationDatePickerDialog
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val EXPIRATION_DATE_PATTERN = "MMM dd, yyyy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationDetailsScreen(
    notificationDetailsState: NotificationDetailsState,
    onRetryClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onCoinFieldClick: (coinId: String) -> Unit,
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

                is UiState.Data -> NotificationForm(
                    coinInfo = notificationDetailsState.coinInfo,
                    notification = state.data,
                    onCoinFieldClick = onCoinFieldClick
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
                modifier = Modifier.basicMarquee(),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationForm(
    coinInfo: CoinInfo,
    notification: Notification?,
    onCoinFieldClick: (coinId: String) -> Unit,
) {
    val formState = rememberNotificationDetailsFormState(coinInfo, notification)
    val focusManager = LocalFocusManager.current

    BoxWithConstraints {
        if (formState.expirationDatePickerVisible) {
            ExpirationDatePickerDialog(
                initialDate = formState.expirationDate,
                displayMode = if (maxHeight > 500.dp) {
                    DisplayMode.Picker
                } else {
                    DisplayMode.Input
                },
                onDateSelected = formState::updateExpirationDate,
                onDismiss = {
                    if (formState.expirationDate == null) {
                        focusManager.clearFocus()
                    }
                    formState.dismissExpirationDatePicker()
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = if (maxWidth > 800.dp) {
                    Alignment.CenterVertically
                } else {
                    Alignment.Top
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoinField(
                name = formState.coinInfo.name,
                onClick = { onCoinFieldClick(coinInfo.id) }
            )
            NotificationTitleField(
                title = formState.notificationTitle,
                onTitleChange = formState::updateNotificationTitle
            )
            TriggerField(
                type = formState.triggerType,
                price = formState.triggerPrice,
                onPriceChange = formState::updateTriggerPrice
            )
            ExpirationDateField(
                date = formatExpirationDate(formState.expirationDate),
                onClick = formState::showExpirationDatePicker
            )
            FormButtons(
                onSaveClick = {
                    // TODO: Validate fields
                },
                onDiscardClick = {
                    // TODO: Show confirmation dialog
                }
            )
        }
    }
}

// TODO: Show coin icon as suffix
@Composable
private fun CoinField(name: String, onClick: () -> Unit) {
    // TODO: Disable if user navigated from CoinHistory screen
    FormField(
        value = TextFieldValue(name),
        label = stringResource(R.string.coin_field_label),
        readOnly = true,
        onClick = onClick
    )
}

@Composable
private fun NotificationTitleField(
    title: TextFieldValue,
    onTitleChange: (title: TextFieldValue) -> Unit,
) {
    FormField(
        value = title,
        onValueChange = onTitleChange,
        label = stringResource(R.string.notification_title_field_label),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
    )
}

@Composable
private fun TriggerField(
    type: NotificationTriggerType,
    price: TextFieldValue,
    onPriceChange: (price: TextFieldValue) -> Unit,
) {
    FormField(
        value = price,
        onValueChange = onPriceChange,
        label = stringResource(R.string.notification_trigger_field_label),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        prefix = {
            TriggerTypeDropdown(type = type)
        },
        suffix = {
            Text(text = "$")
        }
    )
}

@Composable
private fun ExpirationDateField(date: String, onClick: () -> Unit) {
    FormField(
        value = TextFieldValue(date),
        label = stringResource(R.string.expiration_date_field_label),
        readOnly = true,
        onClick = onClick
    )
}

@Composable
private fun FormButtons(
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val buttonModifier = Modifier
            .heightIn(min = 45.dp)
            .weight(1f)

        OutlinedButton(
            modifier = buttonModifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            onClick = onDiscardClick,
            content = {
                Text(text = stringResource(R.string.discard_btn_label))
            }
        )

        OutlinedButton(
            modifier = buttonModifier,
            onClick = onSaveClick,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            content = {
                Text(text = stringResource(R.string.save_btn_label))
            }
        )
    }
}

@Composable
private fun TriggerTypeDropdown(type: NotificationTriggerType) {
    Text(
        modifier = Modifier
            .padding(end = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = {
                    // TODO: Open trigger type dropdown menu
                }
            ),
        text = type.label
    )
}

@Composable
private fun FormField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit = {},
    label: String,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClick?.invoke()
            }
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource.takeIf { onClick != null },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        readOnly = readOnly,
        singleLine = true,
        label = { Text(label) },
        prefix = prefix,
        suffix = suffix
    )
}

@Composable
private fun formatExpirationDate(date: LocalDate?): String {
    if (date == null) return ""
    val locale = getCurrentLocale()
    val dateFormatter = DateTimeFormatter.ofPattern(EXPIRATION_DATE_PATTERN, locale)
    return date.format(dateFormatter)
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
            onCoinFieldClick = {},
            onBackActionClick = {}
        )
    }
}