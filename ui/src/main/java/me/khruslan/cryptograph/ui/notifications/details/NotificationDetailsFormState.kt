package me.khruslan.cryptograph.ui.notifications.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import java.time.LocalDate

internal interface NotificationDetailsFormState {
    val coinInfo: CoinInfo
    val notificationTitle: TextFieldValue
    val triggerType: NotificationTriggerType
    val triggerPrice: TextFieldValue
    val expirationDate: LocalDate?
    val expirationDatePickerVisible: Boolean

    fun updateNotificationTitle(title: TextFieldValue)
    fun updateTriggerPrice(price: TextFieldValue)
    fun updateExpirationDate(date: LocalDate?)
    fun showExpirationDatePicker()
    fun dismissExpirationDatePicker()
}

private class NotificationDetailsFormStateImpl(
    coinInfo: CoinInfo,
    notificationTitle: TextFieldValue,
    triggerType: NotificationTriggerType,
    triggerPrice: TextFieldValue,
    expirationDate: LocalDate?,
    expirationDatePickerVisible: Boolean,
) : NotificationDetailsFormState {

    constructor(
        coinInfo: CoinInfo,
        notification: Notification?,
    ) : this(
        coinInfo = coinInfo,
        notificationTitle = notification.titleTextFieldValue,
        triggerType = notification.triggerType,
        triggerPrice = notification.priceTextFieldValue,
        expirationDate = notification?.expirationDate,
        expirationDatePickerVisible = false
    )

    override var coinInfo by mutableStateOf(coinInfo)
    override var notificationTitle by mutableStateOf(notificationTitle)
    override var triggerType by mutableStateOf(triggerType)
    override var triggerPrice by mutableStateOf(triggerPrice)
    override var expirationDate by mutableStateOf(expirationDate)
    override var expirationDatePickerVisible by mutableStateOf(expirationDatePickerVisible)

    override fun updateNotificationTitle(title: TextFieldValue) {
        notificationTitle = title
    }

    override fun updateTriggerPrice(price: TextFieldValue) {
        triggerPrice = price
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
}

internal enum class NotificationTriggerType(val label: String) {
    PriceLessThen("<"),
    PriceMoreThen(">")
}

@Composable
internal fun rememberNotificationDetailsFormState(
    coinInfo: CoinInfo,
    notification: Notification?,
): NotificationDetailsFormState {
    return rememberSaveable(notification, saver = NotificationDetailsFormStateSaver) {
        NotificationDetailsFormStateImpl(coinInfo, notification)
    }.apply {
        this.coinInfo = coinInfo
    }
}

private val NotificationDetailsFormStateSaver = run {
    val coinInfoKey = "coin_info"
    val notificationTitleKey = "notification_title"
    val triggerTypeKey = "trigger_type"
    val triggerPriceKey = "trigger_price"
    val expirationDateKey = "expiration_date"
    val expirationDatePickerVisibleKey = "expiration_date_picker_visible"

    mapSaver(
        save = { state ->
            mapOf(
                coinInfoKey to state.coinInfo,
                notificationTitleKey to with(TextFieldValue.Saver) {
                    save(state.notificationTitle)
                },
                triggerTypeKey to state.triggerType,
                triggerPriceKey to with(TextFieldValue.Saver) {
                    save(state.triggerPrice)
                },
                expirationDateKey to state.expirationDate,
                expirationDatePickerVisibleKey to state.expirationDatePickerVisible
            )
        },
        restore = { args ->
            NotificationDetailsFormStateImpl(
                coinInfo = args[coinInfoKey] as CoinInfo,
                notificationTitle = checkNotNull(
                    TextFieldValue.Saver.restore(checkNotNull(args[notificationTitleKey]))
                ),
                triggerType = args[triggerTypeKey] as NotificationTriggerType,
                triggerPrice = checkNotNull(
                    TextFieldValue.Saver.restore(checkNotNull(args[triggerPriceKey]))
                ),
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