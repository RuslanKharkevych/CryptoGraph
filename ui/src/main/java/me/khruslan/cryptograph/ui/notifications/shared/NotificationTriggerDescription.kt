package me.khruslan.cryptograph.ui.notifications.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.khruslan.cryptograph.data.notifications.repository.NotificationTrigger
import me.khruslan.cryptograph.ui.R

internal val NotificationTrigger.description: String
    @Composable
    get() {
        val resId = when (this) {
            is NotificationTrigger.PriceLessThan ->
                R.string.notification_trigger_price_less_than_desc

            is NotificationTrigger.PriceMoreThan ->
                R.string.notification_trigger_price_more_than_desc
        }
        val priceString = targetPrice.toBigDecimal().toPlainString()

        return stringResource(resId, priceString)
    }