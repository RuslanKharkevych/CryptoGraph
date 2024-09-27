package me.khruslan.cryptograph.ui.notifications.details

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import java.time.Clock
import java.time.LocalDate

internal interface NotificationDetailsFormState {
    val notificationId: Long
    val coinInfo: CoinInfo
    val notificationTitle: TextFieldValue
    val notificationTitleState: NotificationTitleState
    val triggerType: NotificationTriggerType
    val triggerTypeDropdownExpanded: Boolean
    val triggerPrice: TextFieldValue
    val triggerPriceState: NotificationTriggerPriceState
    val expirationDate: LocalDate?
    val expirationDatePickerVisible: Boolean

    fun updateNotificationTitle(title: TextFieldValue)
    fun validateNotificationTitle(isFocused: Boolean)
    fun updateTriggerType(type: NotificationTriggerType)
    fun expandTriggerTypeDropdown()
    fun collapseTriggerTypeDropdown()
    fun updateTriggerPrice(price: TextFieldValue)
    fun validateTriggerPrice(isFocused: Boolean)
    fun updateExpirationDate(date: LocalDate?)
    fun showExpirationDatePicker()
    fun dismissExpirationDatePicker()
    fun buildNotification(onSuccess: (notification: Notification) -> Unit)
}

@VisibleForTesting
internal class NotificationDetailsFormStateImpl(
    private val clock: Clock,
    notificationId: Long?,
    coinInfo: CoinInfo,
    notificationTitle: TextFieldValue,
    notificationTitleState: NotificationTitleState,
    triggerType: NotificationTriggerType,
    triggerPrice: TextFieldValue,
    triggerPriceState: NotificationTriggerPriceState,
    expirationDate: LocalDate?,
    expirationDatePickerVisible: Boolean,
) : NotificationDetailsFormState {

    constructor(
        coinInfo: CoinInfo,
        notification: Notification?,
        clock: Clock,
    ) : this(
        clock = clock,
        notificationId = notification?.id,
        coinInfo = coinInfo,
        notificationTitle = notification.titleTextFieldValue,
        notificationTitleState = NotificationTitleState.Default,
        triggerType = notification.triggerType,
        triggerPrice = notification.priceTextFieldValue,
        triggerPriceState = NotificationTriggerPriceState.Default,
        expirationDate = notification?.expirationDate,
        expirationDatePickerVisible = false
    )

    override val notificationId = notificationId ?: 0L
    override var coinInfo by mutableStateOf(coinInfo)
    override var notificationTitle by mutableStateOf(notificationTitle)
    override var notificationTitleState by mutableStateOf(notificationTitleState)
    override var triggerType by mutableStateOf(triggerType)
    override var triggerTypeDropdownExpanded by mutableStateOf(false)
    override var triggerPrice by mutableStateOf(triggerPrice)
    override var triggerPriceState by mutableStateOf(triggerPriceState)
    override var expirationDate by mutableStateOf(expirationDate)
    override var expirationDatePickerVisible by mutableStateOf(expirationDatePickerVisible)

    private var triggerPriceFocused by mutableStateOf(false)

    override fun updateNotificationTitle(title: TextFieldValue) {
        notificationTitle = title
    }

    override fun validateNotificationTitle(isFocused: Boolean) {
        notificationTitleState = if (isFocused) {
            NotificationTitleState.Default
        } else {
            getNotificationTitleState(notificationTitle.text)
        }
    }

    override fun updateTriggerType(type: NotificationTriggerType) {
        triggerType = type
        validateTriggerPrice(triggerPriceFocused)
    }

    override fun expandTriggerTypeDropdown() {
        triggerTypeDropdownExpanded = true
    }

    override fun collapseTriggerTypeDropdown() {
        triggerTypeDropdownExpanded = false
    }

    override fun updateTriggerPrice(price: TextFieldValue) {
        if (price.text.all { it.isDigit() || it == '.' }) {
            triggerPrice = price
        }
    }

    override fun validateTriggerPrice(isFocused: Boolean) {
        triggerPriceFocused = isFocused

        triggerPriceState = if (isFocused) {
            NotificationTriggerPriceState.Default
        } else {
            getTriggerPriceState(triggerPrice.text)
        }
    }

    override fun updateExpirationDate(date: LocalDate?) {
        expirationDate = date
    }

    override fun showExpirationDatePicker() {
        expirationDatePickerVisible = true
    }

    override fun dismissExpirationDatePicker() {
        expirationDatePickerVisible = false
    }

    override fun buildNotification(onSuccess: (notification: Notification) -> Unit) {
        val notificationTitle = notificationTitle.text
        val triggerPrice = triggerPrice.text
        val notificationTitleState = getNotificationTitleState(notificationTitle)
        val triggerPriceState = getTriggerPriceState(triggerPrice)

        this.notificationTitleState = notificationTitleState
        this.triggerPriceState = triggerPriceState
        if (!notificationTitleState.isValid || !triggerPriceState.isValid) return

        val notification = Notification(
            id = notificationId,
            coinId = coinInfo.id,
            title = notificationTitle,
            createdAt = LocalDate.now(clock),
            expirationDate = expirationDate,
            trigger = buildTrigger(triggerType, triggerPrice.toDouble())
        )

        onSuccess(notification)
    }

    private fun getNotificationTitleState(titleText: String): NotificationTitleState {
        return if (titleText.isBlank()) {
            NotificationTitleState.Blank
        } else {
            NotificationTitleState.Default
        }
    }

    private fun getTriggerPriceState(priceText: String): NotificationTriggerPriceState {
        if (priceText.isEmpty()) {
            return NotificationTriggerPriceState.Empty
        }

        val price = priceText.toDoubleOrNull()
            ?: return NotificationTriggerPriceState.InvalidFormat

        val currentCoinPrice = coinInfo.price?.removePrefix("$")?.toDoubleOrNull()
            ?: return NotificationTriggerPriceState.Default

        return when (triggerType) {
            NotificationTriggerType.PriceLessThen -> {
                if (price <= currentCoinPrice) {
                    NotificationTriggerPriceState.Default
                } else {
                    NotificationTriggerPriceState.PriceTooBig
                }
            }

            NotificationTriggerType.PriceMoreThen -> {
                if (price >= currentCoinPrice) {
                    NotificationTriggerPriceState.Default
                } else {
                    NotificationTriggerPriceState.PriceTooSmall
                }
            }
        }
    }

    private fun buildTrigger(type: NotificationTriggerType, price: Double): NotificationTrigger {
        return when (type) {
            NotificationTriggerType.PriceLessThen -> NotificationTrigger.PriceLessThen(price)
            NotificationTriggerType.PriceMoreThen -> NotificationTrigger.PriceMoreThen(price)
        }
    }
}

internal enum class NotificationTriggerType(val label: String) {
    PriceLessThen("<"),
    PriceMoreThen(">")
}

internal enum class NotificationTitleState(val isValid: Boolean) {
    Default(true),
    Blank(false)
}

internal enum class NotificationTriggerPriceState(val isValid: Boolean) {
    Default(true),
    Empty(false),
    InvalidFormat(false),
    PriceTooBig(false),
    PriceTooSmall(false)
}

@Composable
internal fun rememberNotificationDetailsFormState(
    coinInfo: CoinInfo,
    notification: Notification?,
): NotificationDetailsFormState {
    val clock = Clock.systemDefaultZone()
    val state = rememberSaveable(notification, saver = NotificationDetailsFormStateSaver) {
        NotificationDetailsFormStateImpl(coinInfo, notification, clock)
    }

    LaunchedEffect(coinInfo) {
        state.coinInfo = coinInfo
        if (state.triggerPrice.text.isNotEmpty()) {
            state.validateTriggerPrice(false)
        }
    }

    return state
}

private val NotificationDetailsFormStateSaver = run {
    val notificationIdKey = "notification_id"
    val coinInfoKey = "coin_info"
    val notificationTitleKey = "notification_title"
    val notificationTitleStateKey = "notification_title_valid_key"
    val triggerTypeKey = "trigger_type"
    val triggerPriceKey = "trigger_price"
    val triggerPriceStateKey = "trigger_price_state"
    val expirationDateKey = "expiration_date"
    val expirationDatePickerVisibleKey = "expiration_date_picker_visible"

    mapSaver(
        save = { state ->
            mapOf(
                notificationIdKey to state.notificationId,
                coinInfoKey to state.coinInfo,
                notificationTitleKey to with(TextFieldValue.Saver) {
                    save(state.notificationTitle)
                },
                notificationTitleStateKey to state.notificationTitleState,
                triggerTypeKey to state.triggerType,
                triggerPriceKey to with(TextFieldValue.Saver) {
                    save(state.triggerPrice)
                },
                triggerPriceStateKey to state.triggerPriceState,
                expirationDateKey to state.expirationDate,
                expirationDatePickerVisibleKey to state.expirationDatePickerVisible
            )
        },
        restore = { args ->
            NotificationDetailsFormStateImpl(
                clock = Clock.systemDefaultZone(),
                notificationId = args[notificationIdKey] as Long,
                coinInfo = args[coinInfoKey] as CoinInfo,
                notificationTitle = checkNotNull(
                    TextFieldValue.Saver.restore(checkNotNull(args[notificationTitleKey]))
                ),
                notificationTitleState = args[notificationTitleStateKey] as NotificationTitleState,
                triggerType = args[triggerTypeKey] as NotificationTriggerType,
                triggerPrice = checkNotNull(
                    TextFieldValue.Saver.restore(checkNotNull(args[triggerPriceKey]))
                ),
                triggerPriceState = args[triggerPriceStateKey] as NotificationTriggerPriceState,
                expirationDate = args[expirationDateKey] as LocalDate?,
                expirationDatePickerVisible = args[expirationDatePickerVisibleKey] as Boolean
            )
        }
    )
}

private val Notification?.titleTextFieldValue
    get() = TextFieldValue(this?.title.orEmpty())

private val Notification?.triggerType
    get() = when (this?.trigger) {
        is NotificationTrigger.PriceLessThen -> NotificationTriggerType.PriceLessThen
        is NotificationTrigger.PriceMoreThen, null -> NotificationTriggerType.PriceMoreThen
    }

private val Notification?.priceTextFieldValue
    get() = TextFieldValue(this?.trigger?.targetPrice?.toBigDecimal()?.toPlainString().orEmpty())