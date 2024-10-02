package me.khruslan.cryptograph.ui.notifications.details

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.khruslan.cryptograph.data.fixtures.PREVIEW_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.notifications.details.confirmation.ConfirmationAlertDialog
import me.khruslan.cryptograph.ui.notifications.details.date.ExpirationDatePickerDialog
import me.khruslan.cryptograph.ui.notifications.shared.deniedOrNeverAsked
import me.khruslan.cryptograph.ui.notifications.shared.rememberNotificationPermissionState
import me.khruslan.cryptograph.ui.util.CurrencyBitcoin
import me.khruslan.cryptograph.ui.util.preview.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import me.khruslan.cryptograph.ui.util.previewPlaceholder
import me.khruslan.cryptograph.ui.util.state.rememberAlertState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val EXPIRATION_DATE_PATTERN = "MMMM dd, yyyy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationDetailsScreen(
    notificationDetailsState: NotificationDetailsState,
    onRetryClick: () -> Unit,
    onSaveNotification: (notification: Notification) -> Unit,
    onDeleteNotification: () -> Unit,
    onWarningShown: () -> Unit,
    onCoinFieldClick: (coinId: String) -> Unit,
    onCloseScreen: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val deleteNotificationAlertState = rememberAlertState()
    val discardChangesAlertState = rememberAlertState()
    val permissionState = rememberNotificationPermissionState(onResult = { onCloseScreen() })

    if (notificationDetailsState.notificationSaved) {
        LaunchedEffect(Unit) {
            if (permissionState.status.deniedOrNeverAsked) {
                permissionState.launchPermissionRequest()
            } else {
                onCloseScreen()
            }
        }
    }

    if (notificationDetailsState.notificationDeleted) {
        LaunchedEffect(Unit) {
            onCloseScreen()
        }
    }

    if (deleteNotificationAlertState.isVisible) {
        ConfirmationAlertDialog(
            message = stringResource(R.string.delete_notification_alert_msg),
            onConfirm = onDeleteNotification,
            onDismiss = deleteNotificationAlertState::dismiss
        )
    }

    if (discardChangesAlertState.isVisible) {
        ConfirmationAlertDialog(
            message = stringResource(R.string.discard_changes_alert_msg),
            onConfirm = onCloseScreen,
            onDismiss = discardChangesAlertState::dismiss
        )
    }

    notificationDetailsState.warningMessageRes?.let { resId ->
        val snackbarMessage = stringResource(resId)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onWarningShown()
        }
    }

    BackHandler {
        discardChangesAlertState.show()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = topBarScrollBehavior,
                title = notificationDetailsState.topBarTitle,
                deleteActionVisible = notificationDetailsState.isDeletable,
                onBackActionClick = discardChangesAlertState::show,
                onDeleteActionClick = deleteNotificationAlertState::show
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
            targetState = notificationDetailsState.notificationState,
            label = "NotificationStateCrossfade"
        ) { state ->
            when (state) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> NotificationForm(
                    coinInfo = notificationDetailsState.coinInfo,
                    isCoinEditable = notificationDetailsState.isCoinEditable,
                    notification = state.data,
                    onCoinFieldClick = onCoinFieldClick,
                    onSaveNotification = onSaveNotification,
                    onDiscardButtonClick = discardChangesAlertState::show
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
    isCoinEditable: Boolean,
    notification: Notification?,
    onCoinFieldClick: (coinId: String) -> Unit,
    onSaveNotification: (notification: Notification) -> Unit,
    onDiscardButtonClick: () -> Unit,
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
                space = 8.dp,
                alignment = if (maxWidth > 800.dp) {
                    Alignment.CenterVertically
                } else {
                    Alignment.Top
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoinField(
                coinInfo = formState.coinInfo,
                editable = isCoinEditable,
                onClick = onCoinFieldClick
            )
            NotificationTitleField(
                title = formState.notificationTitle,
                state = formState.notificationTitleState,
                onTitleChange = formState::updateNotificationTitle,
                onFocusChange = formState::validateNotificationTitle
            )
            TriggerField(
                type = formState.triggerType,
                typeDropdownExpanded = formState.triggerTypeDropdownExpanded,
                price = formState.triggerPrice,
                priceState = formState.triggerPriceState,
                currentCoinPrice = formState.coinInfo.price,
                onPriceChange = formState::updateTriggerPrice,
                onPriceFocusChange = formState::validateTriggerPrice,
                onExpandTypeDropdown = formState::expandTriggerTypeDropdown,
                onCollapseTypeDropdown = formState::collapseTriggerTypeDropdown,
                onTypeSelected = formState::updateTriggerType
            )
            ExpirationDateField(
                date = formatExpirationDate(formState.expirationDate),
                onClick = formState::showExpirationDatePicker
            )
            FormButtons(
                onSaveClick = {
                    focusManager.clearFocus()
                    formState.buildNotification(onSuccess = onSaveNotification)
                },
                onDiscardClick = onDiscardButtonClick
            )
        }
    }
}

@Composable
private fun CoinField(
    coinInfo: CoinInfo,
    editable: Boolean,
    onClick: (coinId: String) -> Unit,
) {
    FormField(
        value = TextFieldValue(coinInfo.name),
        label = stringResource(R.string.coin_field_label),
        supportingText = stringResource(
            R.string.coin_field_desc,
            coinInfo.price ?: unknownPricePlaceholder()
        ),
        readOnly = true,
        enabled = editable,
        suffix = {
            AsyncImage(
                modifier = Modifier
                    .size(24.dp)
                    .alpha(if (editable) 1f else 0.5f),
                model = coinInfo.iconUrl,
                contentDescription = stringResource(R.string.coin_icon_desc, coinInfo.name),
                placeholder = previewPlaceholder(Icons.Default.CurrencyBitcoin)
            )
        },
        onClick = { onClick(coinInfo.id) }
    )
}

@Composable
private fun NotificationTitleField(
    title: TextFieldValue,
    state: NotificationTitleState,
    onTitleChange: (title: TextFieldValue) -> Unit,
    onFocusChange: (isFocused: Boolean) -> Unit,
) {
    FormField(
        value = title,
        onValueChange = onTitleChange,
        label = stringResource(R.string.notification_title_field_label),
        supportingText = stringResource(
            when (state) {
                NotificationTitleState.Default -> R.string.notification_title_field_desc
                NotificationTitleState.Blank -> R.string.notification_title_field_blank_error_label
            }
        ),
        isError = !state.isValid,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        onFocusChange = onFocusChange
    )
}

@Composable
private fun TriggerField(
    type: NotificationTriggerType,
    typeDropdownExpanded: Boolean,
    price: TextFieldValue,
    priceState: NotificationTriggerPriceState,
    currentCoinPrice: String?,
    onPriceChange: (price: TextFieldValue) -> Unit,
    onPriceFocusChange: (isFocused: Boolean) -> Unit,
    onExpandTypeDropdown: () -> Unit,
    onCollapseTypeDropdown: () -> Unit,
    onTypeSelected: (type: NotificationTriggerType) -> Unit,
) {
    FormField(
        value = price,
        onValueChange = onPriceChange,
        label = stringResource(R.string.notification_trigger_field_label),
        supportingText = getTriggerPriceSupportingLabel(
            state = priceState,
            currentCoinPrice = currentCoinPrice
        ),
        isError = !priceState.isValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        prefix = {
            TriggerTypeDropdown(
                type = type,
                expanded = typeDropdownExpanded,
                onExpand = onExpandTypeDropdown,
                onCollapse = onCollapseTypeDropdown,
                onTypeSelected = onTypeSelected
            )
        },
        suffix = {
            Text(text = "$")
        },
        onFocusChange = onPriceFocusChange
    )
}

@Composable
private fun ExpirationDateField(date: String, onClick: () -> Unit) {
    FormField(
        value = TextFieldValue(date),
        label = stringResource(R.string.expiration_date_field_label),
        supportingText = stringResource(R.string.expiration_date_field_desc),
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
private fun TriggerTypeDropdown(
    type: NotificationTriggerType,
    expanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onTypeSelected: (type: NotificationTriggerType) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(end = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = onExpand
            ),
        text = type.label
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onCollapse
    ) {
        NotificationTriggerType.entries.forEach { type ->
            TextButton(
                onClick = {
                    onTypeSelected(type)
                    onCollapse()
                },
                content = {
                    Text(text = type.label)
                }
            )
        }
    }
}

@Composable
private fun FormField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit = {},
    label: String,
    supportingText: String,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onFocusChange: ((isFocused: Boolean) -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val onClickListener by rememberUpdatedState(onClick)
    var wasFocused by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClickListener?.invoke()
            }
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                val isFocused = focusState.isFocused
                if (isFocused) wasFocused = true
                if (wasFocused) onFocusChange?.invoke(isFocused)
            },
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource.takeIf { onClick != null },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        colors = formFieldColors(),
        readOnly = readOnly,
        enabled = enabled,
        isError = isError,
        singleLine = true,
        label = { Text(label) },
        prefix = prefix,
        suffix = suffix,
        supportingText = { Text(supportingText) }
    )
}

@Composable
private fun formFieldColors(): TextFieldColors {
    val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    return OutlinedTextFieldDefaults.colors(
        disabledBorderColor = disabledColor,
        disabledTextColor = disabledColor,
        disabledLabelColor = disabledColor
    )
}

@Composable
private fun formatExpirationDate(date: LocalDate?): String {
    if (date == null) return ""
    val locale = getCurrentLocale()
    val dateFormatter = DateTimeFormatter.ofPattern(EXPIRATION_DATE_PATTERN, locale)
    return date.format(dateFormatter)
}

@Composable
private fun getTriggerPriceSupportingLabel(
    state: NotificationTriggerPriceState,
    currentCoinPrice: String?,
): String {
    val price = currentCoinPrice ?: unknownPricePlaceholder()

    return when (state) {
        NotificationTriggerPriceState.Default -> stringResource(
            R.string.notification_trigger_field_desc
        )

        NotificationTriggerPriceState.Empty -> stringResource(
            R.string.notification_trigger_field_empty_price_error_label
        )

        NotificationTriggerPriceState.InvalidFormat -> stringResource(
            R.string.notification_trigger_field_invalid_price_error_label
        )

        NotificationTriggerPriceState.PriceTooSmall -> stringResource(
            R.string.notification_trigger_field_price_too_small_error_label,
            price
        )

        NotificationTriggerPriceState.PriceTooBig -> stringResource(
            R.string.notification_trigger_field_price_too_big_error_label,
            price
        )
    }
}

@Composable
private fun unknownPricePlaceholder(): String {
    return stringResource(R.string.notification_trigger_price_unknown_placeholder)
}

private val NotificationDetailsState.topBarTitle
    @Composable
    get() = notificationTitle ?: if (isCoinEditable) {
        stringResource(R.string.new_generic_notification_title)
    } else {
        stringResource(R.string.new_coin_notification_title, coinInfo.name)
    }

@Composable
@PreviewScreenSizesLightDark
private fun NotificationDetailsScreenPreview() {
    val notificationState = remember {
        val args = NotificationDetailsArgs(
            notificationId = 5L,
            notificationTitle = "Solana > 200$",
            coinId = "zNZHO_Sjf",
            coinName = "Solana",
            coinPrice = "$136.43",
            coinIconUrl = "https://cdn.coinranking.com/yvUG4Qex5/solana.svg",
            coinEditable = true
        )

        MutableNotificationDetailsState(args).apply {
            notificationState = UiState.Data(PREVIEW_NOTIFICATIONS[4])
        }
    }

    CryptoGraphTheme {
        NotificationDetailsScreen(
            notificationDetailsState = notificationState,
            onRetryClick = {},
            onSaveNotification = {},
            onDeleteNotification = {},
            onWarningShown = {},
            onCoinFieldClick = {},
            onCloseScreen = {}
        )
    }
}