package me.khruslan.cryptograph.ui.notifications.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import java.time.LocalDate

// TODO: Save state on configuration changes
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
    notification: Notification?,
) : NotificationDetailsFormState {

    override var coinInfo by mutableStateOf(coinInfo)
    override var notificationTitle by notificationTitleState(notification)
    override var triggerType by triggerTypeState(notification)
    override var triggerPrice by triggerPriceState(notification)
    override var expirationDate by mutableStateOf(notification?.expirationDate)
    override var expirationDatePickerVisible by mutableStateOf(false)

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

    private fun notificationTitleState(notification: Notification?): MutableState<TextFieldValue> {
        return mutableStateOf(TextFieldValue(notification?.title.orEmpty()))
    }

    private fun triggerTypeState(
        notification: Notification?,
    ): MutableState<NotificationTriggerType> {
        val type = when (notification?.trigger) {
            is NotificationTrigger.PriceLessThen -> NotificationTriggerType.PriceLessThen
            is NotificationTrigger.PriceMoreThen, null -> NotificationTriggerType.PriceMoreThen
        }
        return mutableStateOf(type)
    }

    private fun triggerPriceState(notification: Notification?): MutableState<TextFieldValue> {
        val price = notification?.trigger?.targetPrice?.toBigDecimal()?.toPlainString().orEmpty()
        return mutableStateOf(TextFieldValue(price))
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
    return remember(notification) {
        NotificationDetailsFormStateImpl(coinInfo, notification)
    }.apply {
        this.coinInfo = coinInfo
    }
}