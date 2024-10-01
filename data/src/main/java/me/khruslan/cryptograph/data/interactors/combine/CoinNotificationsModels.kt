package me.khruslan.cryptograph.data.interactors.combine

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.notifications.Notification

data class CoinNotification(
    val coin: Coin,
    val notification: Notification
)