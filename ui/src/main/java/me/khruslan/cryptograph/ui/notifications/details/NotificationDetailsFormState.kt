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
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val EXPIRATION_DATE_PATTERN = "MMM dd, yyyy"

internal interface NotificationDetailsFormState {
    val coinInfo: CoinInfo
    val notificationTitle: TextFieldValue
    val triggerType: NotificationTriggerType
    val triggerPrice: TextFieldValue
    val expirationDate: String

    fun updateNotificationTitle(title: TextFieldValue)
    fun updateTriggerPrice(price: TextFieldValue)
    fun showDatePicker()
}

private class NotificationDetailsFormStateImpl(
    coinInfo: CoinInfo,
    notification: Notification?,
    locale: Locale,
) : NotificationDetailsFormState {

    private val expirationDateFormatter =
        DateTimeFormatter.ofPattern(EXPIRATION_DATE_PATTERN, locale)

    override var coinInfo by mutableStateOf(coinInfo)
    override var notificationTitle by notificationTitleState(notification)
    override var triggerType by triggerTypeState(notification)
    override var triggerPrice by triggerPriceState(notification)
    override var expirationDate by expirationDateState(notification)

    override fun updateNotificationTitle(title: TextFieldValue) {
        notificationTitle = title
    }

    override fun updateTriggerPrice(price: TextFieldValue) {
        triggerPrice = price
    }

    override fun showDatePicker() {
        // TODO: Show date picker
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

    private fun expirationDateState(notification: Notification?): MutableState<String> {
        val dateString = notification?.expirationDate?.format(expirationDateFormatter).orEmpty()
        return mutableStateOf(dateString)
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
    val locale = getCurrentLocale()

    return remember(notification) {
        NotificationDetailsFormStateImpl(coinInfo, notification, locale)
    }.apply {
        this.coinInfo = coinInfo
    }
}